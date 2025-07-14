package com.example.demo.service.impl;

import com.example.demo.constants.RecommendConstants;
import com.example.demo.entity.User;
import com.example.demo.entity.Video;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.mapper.VideoTagMapper;
import com.example.demo.mapper.VideoTagRelationMapper;
import com.example.demo.service.VideoPlaybackService;
import com.example.demo.vo.AuthorSimpleVO;
import com.example.demo.vo.RecommendVideoVO;
import com.example.demo.vo.VideoDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoPlaybackServiceImpl implements VideoPlaybackService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoTagMapper videoTagMapper;
    // 在服务类中注入用户Mapper
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VideoTagRelationMapper tagRelationMapper;

    // 功能一：获取视频基本信息
    @Override
    public VideoDetailVO getVideoDetail(Integer videoId) {
        Video video = videoMapper.selectVideoWithoutStatus(videoId);
        if (video == null) {
            throw new RuntimeException("视频不存在或已被删除");
        }
        return convertToDetailVO(video);
    }

    // 功能二：获取视频标签
    @Override
    public List<String> getVideoTags(Integer videoId) {
        return videoTagMapper.selectTagsByVideoId(videoId);
    }

    // 功能三：获取推荐视频
    @Override
    public List<RecommendVideoVO> getRelatedRecommendVideos(Integer videoId) {
        // 1. 获取当前视频标签
        List<String> currentTags = getVideoTags(videoId);
        if (currentTags.isEmpty()) {
            return getDefaultRecommendations();
        }

        // 2. 执行推荐算法
        return videoMapper.selectRelatedRecommendVideos(
                videoId,
                currentTags,
                RecommendConstants.TAG_WEIGHT,
                RecommendConstants.PLAY_WEIGHT,
                RecommendConstants.RECOMMEND_LIMIT
        );
    }
    // 功能四：视频播放量增加
    @Override
    @Transactional
    public void incrementViewCount(Integer videoId) {
        // 1. 获取视频作者ID
        Integer authorId = videoMapper.selectAuthorIdByVideoId(videoId);
        if (authorId == null) {
            throw new RuntimeException("视频不存在");
        }
        // 2. 原子更新视频播放量
        int affectedRows = videoMapper.incrementViewCount(videoId);
        if (affectedRows == 0) {
            throw new RuntimeException("视频播放量更新失败");
        }
        // 3. 原子更新作者总播放量
        userMapper.incrementViewCount(authorId);
    }

    // 默认推荐（无标签时使用）
    private List<RecommendVideoVO> getDefaultRecommendations() {
        return videoMapper.selectHotVideos(
                RecommendConstants.DEFAULT_RECOMMEND_LIMIT
        );
    }

    // 转换视频详情VO
    private VideoDetailVO convertToDetailVO(Video video) {
        VideoDetailVO vo = new VideoDetailVO();
        BeanUtils.copyProperties(video, vo);
        vo.setAuthorInfo(getAuthorInfo(video.getAuthorId()));
        return vo;
    }

    // 获取作者信息
    private AuthorSimpleVO getAuthorInfo(Integer authorId) {
        // 1. 查询用户基本信息
        User author = userMapper.selectById(authorId);
        if (author == null) {
            // 处理用户不存在的情况
            return new AuthorSimpleVO(authorId, "已注销用户", "default_avatar.png", "");
        }
        // 2. 构建VO对象
        return new AuthorSimpleVO(
                author.getUserId(),
                author.getUserName(),
                author.getAvatar(),
                author.getSignature() != null ? author.getSignature() : ""
        );
    }
}
