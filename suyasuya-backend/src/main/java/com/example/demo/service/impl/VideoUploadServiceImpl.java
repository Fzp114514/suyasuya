package com.example.demo.service.impl;

import com.example.demo.config.FfmpegConfig;
import com.example.demo.dto.VideoPublishDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.service.VideoUploadService;
import com.example.demo.utils.VideoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class VideoUploadServiceImpl implements VideoUploadService {

    @Value("${upload.video.path}")
    private String videoUploadDir;
    @Value("${upload.cover.path}")
    private String coverUploadDir;
    @Value("${upload.temporarily.path}")
    private String tempUploadDir;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoCategoryMapper categoryMapper;
    @Autowired
    private VideoTagMapper tagMapper;
    @Autowired
    private VideoTagRelationMapper tagRelationMapper;
    @Autowired
    private UserMapper userMapper;

    // 处理视频临时上传
    @Override
    public Map<String, Object> uploadVideoTemporary(Integer authorId, MultipartFile file) {
        // 1. 校验文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isValidVideoExtension(extension)) {
            throw new IllegalArgumentException("仅支持 mp4/avi/mkv 格式视频");
        }

        // 2. 生成唯一存储路径
        Path filePath = getUniqueFilePath(videoUploadDir, extension);
        String newFileName = filePath.getFileName().toString();

        try {
            // 3. 保存文件到服务器
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());

        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
        // 获取视频时长
        int duration = VideoUtil.getVideoDuration(filePath);
        if (duration <= 0) {
            throw new RuntimeException("无法解析视频时长" + filePath);
        }

        // 4. 获取当前用户信息（示例逻辑）
        User author = userMapper.selectById(authorId);
        if (author == null) {
            throw new RuntimeException("用户不存在");
        }

        // 5. 创建临时视频记录
        Video video = new Video();
        video.setAuthorId(authorId);
        video.setAuthorName(author.getUserName());
        video.setVideo(newFileName);
        video.setDuration(duration);
        video.setStatus("temporary"); // 标记为临时状态
        videoMapper.insert(video);

        // 6. 返回视频ID和URL
        return Map.of(
                "videoId", video.getVideoId(),
                "videoUrl", "/video/" + newFileName
        );
    }

    /**
     * 处理视频投稿（事务方法）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishVideo(VideoPublishDTO publishDTO) throws IOException {
        // 1. 验证并获取临时视频记录
        Video video = validateAndGetVideo(publishDTO.getVideoId());

        // 2. 处理封面图片
        String coverPath = processCoverImage(publishDTO.getCoverFile());
        video.setCover(coverPath);

        // 3. 处理视频分类
        validateCategory(publishDTO.getCategoryId());

        // 4. 更新视频基础信息
        updateVideoBasicInfo(video, publishDTO);

        // 5. 处理视频标签
        processVideoTags(video.getVideoId(), publishDTO.getTags());

        // 6. 更新视频状态为已发布
        video.setStatus("published");
        video.setUploadTime(new Date());
        videoMapper.updateById(video);
    }

    /**
     * 验证并获取视频记录
     */
    private Video validateAndGetVideo(Integer videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new RuntimeException("视频不存在");
        }
        if (!"temporary".equals(video.getStatus())) {
            throw new RuntimeException("视频状态异常,非临时视频");
        }
        return video;
    }

    /**
     * 处理封面图片上传
     */
    private String processCoverImage(MultipartFile coverFile) throws IOException {
        if (coverFile == null || coverFile.isEmpty()) {
            throw new RuntimeException("封面图片不能为空");
        }

        // 验证图片格式
        String extension = getFileExtension(coverFile.getOriginalFilename());
        if (!isValidImageExtension(extension)) {
            throw new RuntimeException("不支持的图片格式");
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID() + "." + extension;
        Path targetPath = Paths.get(coverUploadDir, fileName);

        // 确保目录存在
        Files.createDirectories(targetPath.getParent());
        coverFile.transferTo(targetPath.toFile());

        return fileName;
    }

    /**
     * 验证视频分类有效性
     */
    private void validateCategory(Integer categoryId) {
        VideoCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("无效的视频分类");
        }
    }

    /**
     * 更新视频基础信息
     */
    private void updateVideoBasicInfo(Video video, VideoPublishDTO dto) {
        video.setTitle(dto.getTitle());
        video.setIntroduction(dto.getIntroduction());
        video.setCategoryId(dto.getCategoryId());
        // 其他字段设置...
    }

    /**
     * 处理视频标签关联
     */
    private void processVideoTags(Integer videoId, List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        // 去重处理
        Set<String> uniqueTags = new HashSet<>(tags);

        for (String tagName : uniqueTags) {
            // 查找或创建标签
            VideoTag tag = tagMapper.selectByName(tagName);
            if (tag == null) {
                tag = createNewTag(tagName);
            }

            // 创建关联关系
            createTagRelation(videoId, tag.getTagId());

            // 更新标签计数
            updateTagRelatedCount(tag.getTagId());
        }
    }

    /**
     * 创建新标签记录
     */
    private VideoTag createNewTag(String tagName) {
        VideoTag newTag = new VideoTag();
        newTag.setTagName(tagName);
        newTag.setRelatedCount(0); // 初始值由关联操作触发
        tagMapper.insert(newTag);
        return newTag;
    }

    /**
     * 创建标签关联关系
     */
    private void createTagRelation(Integer videoId, Integer tagId) {
        if (tagRelationMapper.existsRelation(videoId, tagId)) {
            return; // 已存在关系则跳过
        }

        VideoTagRelation relation = new VideoTagRelation();
        relation.setVideoId(videoId);
        relation.setTagId(tagId);
        tagRelationMapper.insert(relation);
    }

    /**
     * 原子更新标签关联计数
     */
    private void updateTagRelatedCount(Integer tagId) {
        tagMapper.incrementRelatedCount(tagId);
    }

    // 获取文件扩展名
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    // 验证视频文件扩展名
    private boolean isValidVideoExtension(String extension) {
        // 将扩展名转为小写，并判断是否符合要求
        String lowerCaseExtension = extension.toLowerCase();
        return lowerCaseExtension.equals("mp4") || lowerCaseExtension.equals("avi") || lowerCaseExtension.equals("mkv");
    }

    // 使用UUID生成唯一的文件保存路径
    private Path getUniqueFilePath(String uploadDir, String fileExtension) {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + fileExtension;
        return Paths.get(uploadDir, fileName);
    }

    // 验证图片文件扩展名
    private boolean isValidImageExtension(String extension) {
        // 将扩展名转为小写，并判断是否符合要求
        String lowerCaseExtension = extension.toLowerCase();
        return lowerCaseExtension.equals("jpg") || lowerCaseExtension.equals("jpeg") || lowerCaseExtension.equals("png");
    }
}
