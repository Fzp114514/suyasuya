package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.PageResult;
import com.example.demo.entity.WatchHistory;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.mapper.WatchHistoryMapper;
import com.example.demo.service.WatchHistoryService;
import com.example.demo.vo.WatchHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class WatchHistoryServiceImpl implements WatchHistoryService {
    @Autowired
    private WatchHistoryMapper watchHistoryMapper;
    private static final int MAX_PAGE_SIZE = 100;


    @Override
    @Transactional
    public void recordWatchHistory(Integer userId, Integer videoId) {
        // 使用UPSERT操作（存在则更新，不存在则插入）
        watchHistoryMapper.upsertWatchHistory(
                userId,
                videoId,
                new Date()
        );
    }

    @Override
    public PageResult<WatchHistoryVO> getWatchHistory(Integer currentUserId,
                                                      Integer pageNum,
                                                      Integer pageSize) {
        // 参数校验
        validateParams(pageNum, pageSize);

        // 创建分页对象
        Page<WatchHistoryVO> page = new Page<>(pageNum, Math.min(pageSize, MAX_PAGE_SIZE));

        // 执行分页查询
        IPage<WatchHistoryVO> result = watchHistoryMapper.selectHistoryPage(page, currentUserId);

        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        );
    }

    @Override
    public void deleteHistory(Integer userId, Integer videoId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }
        if (videoId == null || videoId <= 0) {
            throw new RuntimeException("无效视频ID");
        }

        // 构建删除条件
        QueryWrapper<WatchHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("video_id", videoId);

        // 执行删除
        int deleted = watchHistoryMapper.delete(wrapper);
        if (deleted == 0) {
            throw new RuntimeException("未找到相关记录");
        }
    }

    private void validateParams(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            throw new RuntimeException("页码参数错误");
        }
        if (pageSize == null || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new RuntimeException("分页大小需在1-" + MAX_PAGE_SIZE + "之间");
        }
    }

    @Transactional
    public void clearHistory(Integer userId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }
        // 构建删除条件
        QueryWrapper<WatchHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        // 执行删除
        int deletedCount = watchHistoryMapper.delete(wrapper);
    }
}
