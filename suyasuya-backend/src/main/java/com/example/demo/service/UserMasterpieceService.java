package com.example.demo.service;

import com.example.demo.entity.Video;

public interface UserMasterpieceService {
    void saveOrUpdateMasterpiece(Integer userId, Integer videoId);

    Video getMasterpieceVideo(Integer userId);
}
