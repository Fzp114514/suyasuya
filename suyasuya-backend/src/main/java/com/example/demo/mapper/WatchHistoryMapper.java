package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.WatchHistory;
import com.example.demo.vo.WatchHistoryVO;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface WatchHistoryMapper extends BaseMapper<WatchHistory> {

    void upsertWatchHistory(
            @Param("userId") Integer userId,
            @Param("videoId") Integer videoId,
            @Param("watchTime") Date watchTime
    );

    boolean videoExists(Integer videoId);

    IPage<WatchHistoryVO> selectHistoryPage(Page<WatchHistoryVO> page, @Param("userId") Integer userId);
}