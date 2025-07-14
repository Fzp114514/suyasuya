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
    @Insert("INSERT INTO watch_history (user_id, video_id, watch_time) " +
            "VALUES (#{userId}, #{videoId}, #{watchTime}) " +
            "ON DUPLICATE KEY UPDATE watch_time = #{watchTime}")
    void upsertWatchHistory(
            @Param("userId") Integer userId,
            @Param("videoId") Integer videoId,
            @Param("watchTime") Date watchTime
    );
    @Select("SELECT EXISTS(SELECT 1 FROM videos WHERE video_id = #{videoId})")
    boolean videoExists(Integer videoId);

    @Select({
            "SELECT v.video_id, v.cover, v.title, v.author_id,",
            "u.user_name AS author_name, wh.watch_time,",
            "v.duration, v.view_count",
            "FROM watch_history wh",
            "LEFT JOIN videos v ON wh.video_id = v.video_id",
            "LEFT JOIN users u ON v.author_id = u.user_id",
            "WHERE wh.user_id = #{userId}",
            "ORDER BY wh.watch_time DESC"
    })
    @Results(id = "watchHistoryVOMap", value = {
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "cover", column = "cover"),
            @Result(property = "title", column = "title"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "watchTime", column = "watch_time"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "viewCount", column = "view_count")
    })
    IPage<WatchHistoryVO> selectHistoryPage(Page<WatchHistoryVO> page, @Param("userId") Integer userId);
}
