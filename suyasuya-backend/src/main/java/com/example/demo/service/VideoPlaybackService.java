package com.example.demo.service;

import com.example.demo.vo.RecommendVideoVO;
import com.example.demo.vo.VideoDetailVO;

import java.util.List;

public interface VideoPlaybackService {
    VideoDetailVO getVideoDetail(Integer videoId);

    List<String> getVideoTags(Integer videoId);

    List<RecommendVideoVO> getRelatedRecommendVideos(Integer videoId);

    void incrementViewCount(Integer videoId);
}
