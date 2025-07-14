package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.VideoDTO;
import com.example.demo.vo.VideoSimpleVO;

import java.util.List;

public interface VideoQueryService {
    List<VideoSimpleVO> getRandomVideos(Integer count);

    List<VideoSimpleVO> getVideosByCategory(Integer categoryId, Integer count);

    Page<VideoDTO> getVideosByUserByUploadTime(Integer userId, int pageNum, int pageSize);

    Page<VideoDTO> getVideosByUserByViewCount(Integer userId, int pageNum, int pageSize);

    Page<VideoDTO> getVideosByUserByCollectionCount(Integer userId, int pageNum, int pageSize);
}
