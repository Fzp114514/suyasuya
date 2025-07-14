package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.dto.VideoDTO;
import com.example.demo.vo.VideoSearchVO;

import java.util.List;

public interface SearchService {
    PageResult<VideoSearchVO> searchVideos(Integer userId,String keyword, int pageNum, int pageSize,String sortType);
}
