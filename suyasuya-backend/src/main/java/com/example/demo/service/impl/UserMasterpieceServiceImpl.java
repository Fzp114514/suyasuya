package com.example.demo.service.impl;

import com.example.demo.entity.UserMasterpiece;
import com.example.demo.entity.Video;
import com.example.demo.mapper.UserMasterpieceMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.UserMasterpieceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserMasterpieceServiceImpl implements UserMasterpieceService {
    @Autowired
    private UserMasterpieceMapper userMasterpieceMapper;
    @Autowired
    private VideoMapper videoMapper;

    @Override
    @Transactional
    public void saveOrUpdateMasterpiece(Integer userId, Integer videoId) {
        if (userId == null || videoId == null) {
            throw new IllegalArgumentException("参数不可为空");
        }

        int affectedRows = userMasterpieceMapper.upsert(userId, videoId);
        if (affectedRows == 0) {
            throw new RuntimeException("操作未生效");
        }
    }

    @Override
    public Video getMasterpieceVideo(Integer userId) {
        // 参数校验
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 1. 查询代表作视频ID
        UserMasterpiece masterpiece = userMasterpieceMapper.selectById(userId);
        if (masterpiece == null || masterpiece.getVideoId() == null) {
            return null; // 没有设置代表作
        }

        // 2. 查询视频详细信息
        Video video = videoMapper.selectById(masterpiece.getVideoId());

        // 3. 校验视频状态（可选）
        if (video == null || !"published".equals(video.getStatus())) {
            throw new IllegalStateException("该视频不可查看");
        }

        return video;
    }
}
