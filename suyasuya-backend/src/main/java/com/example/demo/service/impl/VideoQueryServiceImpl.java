package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.VideoDTO;
import com.example.demo.entity.Video;
import com.example.demo.mapper.VideoCategoryMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.VideoQueryService;
import com.example.demo.vo.VideoSimpleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VideoQueryServiceImpl implements VideoQueryService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoCategoryMapper categoryMapper;

    // 功能一：获取随机视频推荐
    @Override
    public List<VideoSimpleVO> getRandomVideos(Integer count) {
        // 参数校验
        if (count == null || count <= 0) {
            throw new IllegalArgumentException("请求数量参数不合法");
        }
        // 获取总视频数
        Integer total = videoMapper.countPublishedVideos();
        // 调整实际请求数量
        int actualCount = Math.min(count, total);
        if (actualCount <= 0) {
            return Collections.emptyList();
        }
        // 随机查询视频
        return videoMapper.selectRandomVideos(actualCount);
    }

    // 功能二：获取分类视频
    @Override
    public List<VideoSimpleVO> getVideosByCategory(Integer categoryId, Integer count) {
        // 参数校验
        if (categoryId == null || count == null || count <= 0) {
            throw new IllegalArgumentException("请求参数不合法");
        }

        // 验证分类是否存在
        if (!categoryMapper.existsById(categoryId)) {
            throw new RuntimeException("视频分类不存在");
        }

        // 获取分类下视频总数
        Integer total = videoMapper.countPublishedVideosByCategory(categoryId);

        // 调整实际请求数量
        int actualCount = Math.min(count, total);
        if (actualCount <= 0) {
            return Collections.emptyList();
        }

        // 随机查询分类视频
        return videoMapper.selectRandomVideosByCategory(categoryId, actualCount);
    }

    /**
     * 根据用户ID查询用户的投稿视频，按上传时间从最近到最久排序
     */
    @Override
    public Page<VideoDTO> getVideosByUserByUploadTime(Integer userId, int pageNum, int pageSize) {
        return getVideosByField(userId, pageNum, pageSize, "upload_time", true);
    }
    /**
     * 根据用户ID查询用户的投稿视频，按播放量排序
     */
    @Override
    public Page<VideoDTO> getVideosByUserByViewCount(Integer userId, int pageNum, int pageSize) {
        return getVideosByField(userId, pageNum, pageSize, "view_count", true);  // 按降序排序播放量
    }
    /**
     * 根据用户ID查询用户的投稿视频，按收藏数排序
     */
    @Override
    public Page<VideoDTO> getVideosByUserByCollectionCount(Integer userId, int pageNum, int pageSize) {
        return getVideosByField(userId, pageNum, pageSize, "collection_count", false);  // 假设按升序排序收藏数
    }
    /**
     * 通用分页查询方法，根据传入的排序字段和顺序来查询
     */
    private Page<VideoDTO> getVideosByField(Integer userId, int pageNum, int pageSize, String orderByField, boolean isDesc) {
        // 创建分页对象，pageNum 是当前页，pageSize 是每页条数
        Page<Video> page = new Page<>(pageNum, pageSize);

        // 创建查询条件
        QueryWrapper<Video> queryWrapper = new QueryWrapper<Video>()
                .eq("author_id", userId)
                .eq("status", "published"); // 过滤出已发布的视频

        // 根据传入的排序字段和顺序动态设置排序
        if (isDesc) {
            queryWrapper.orderByDesc(orderByField);
        } else {
            queryWrapper.orderByAsc(orderByField);
        }

        // 执行分页查询
        Page<Video> resultPage = videoMapper.selectPage(page, queryWrapper);

        // 创建分页 DTO 返回
        Page<VideoDTO> videoDTOPage = new Page<>();
        videoDTOPage.setRecords(convertToDTO(resultPage.getRecords()));
        videoDTOPage.setTotal(resultPage.getTotal());
        videoDTOPage.setCurrent(resultPage.getCurrent());
        videoDTOPage.setSize(resultPage.getSize());
        videoDTOPage.setPages(resultPage.getPages());

        return videoDTOPage;
    }

    /**
     * 将 Video 转换为 VideoDTO
     */
    private List<VideoDTO> convertToDTO(List<Video> videos) {
        List<VideoDTO> videoDTOs = new ArrayList<>();
        for (Video video : videos) {
            VideoDTO videoDTO = new VideoDTO();
            videoDTO.setVideoId(video.getVideoId());
            videoDTO.setTitle(video.getTitle());
            videoDTO.setCover(video.getCover());
            videoDTO.setDuration(video.getDuration());
            videoDTO.setViewCount(video.getViewCount());
            videoDTO.setUploadTime(video.getUploadTime());
            videoDTO.setCollectionCount(video.getCollectionCount());
            videoDTO.setIntroduction(video.getIntroduction());
            videoDTOs.add(videoDTO);
        }
        return videoDTOs;
    }
}
