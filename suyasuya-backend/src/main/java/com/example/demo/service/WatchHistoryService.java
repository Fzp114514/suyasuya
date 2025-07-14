package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.vo.WatchHistoryVO;

public interface WatchHistoryService {
    void recordWatchHistory(Integer userId, Integer videoId);
    PageResult<WatchHistoryVO> getWatchHistory(Integer currentUserId,
                                               Integer pageNum,
                                               Integer pageSize);
    void deleteHistory(Integer userId, Integer videoId);
    void clearHistory(Integer userId);
}
