package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Video;
import com.example.demo.vo.RecommendVideoVO;
import com.example.demo.vo.VideoSearchVO;
import com.example.demo.vo.VideoSimpleVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    @Update("UPDATE videos SET author_name = #{newAuthorName} WHERE author_id = #{userId}")
    void updateAuthorNameByUserId(@Param("userId") Integer userId,
                                  @Param("newAuthorName") String newAuthorName);

    // UserMapper.java 新增存在性检查
    @Select("SELECT COUNT(*) FROM users WHERE user_name = #{userName}")
    boolean existsByUserName(String userName);

    @Select("SELECT author_id FROM videos WHERE video_id = #{videoId}")
    Integer selectAuthorIdByVideoId(Integer videoId);

    @Update("UPDATE videos SET view_count = view_count + 1 WHERE video_id = #{videoId}")
    int incrementViewCount(Integer videoId);

    @Update("UPDATE videos SET collection_count = collection_count + #{increment} " +
            "WHERE video_id = #{videoId}")
    int updateCollectionCount(@Param("videoId") Integer videoId,
                              @Param("increment") int increment);

//    ===============================================================

    // 随机查询视频（功能一）
    @Select("SELECT video_id, author_id, author_name, upload_time, title, duration, cover, view_count " +
            "FROM videos WHERE status = 'published' " +
            "ORDER BY RAND() LIMIT #{count}")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "uploadTime", column = "upload_time"),
    })
    List<VideoSimpleVO> selectRandomVideos(Integer count);

    // 按视频分类随机查询（功能二）
    @Select("SELECT video_id, author_id, author_name, upload_time, title, duration, cover, view_count " +
            "FROM videos WHERE status = 'published' AND category_id = #{categoryId} " +
            "ORDER BY RAND() LIMIT #{count}")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "authorId", column = "author_id")
    })
    List<VideoSimpleVO> selectRandomVideosByCategory(
            @Param("categoryId") Integer categoryId,
            @Param("count") Integer count
    );

    // 统计已发布视频总数
    @Select("SELECT COUNT(*) FROM videos WHERE status = 'published'")
    Integer countPublishedVideos();

    // 统计分类下已发布视频数
    @Select("SELECT COUNT(*) FROM videos " +
            "WHERE status = 'published' AND category_id = #{categoryId}")
    Integer countPublishedVideosByCategory(Integer categoryId);

//    ======================= 视频播放模块相关 =======================================

    // 功能一：查询视频基本信息（排除status字段）
    @Select("SELECT video_id, video, author_id, author_name, upload_time, title, duration, cover, " +
            "introduction, category_id, like_count, view_count, comment_count, " +
            "collection_count FROM videos WHERE video_id = #{videoId}")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "uploadTime", column = "upload_time"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "collectionCount", column = "collection_count")
    })
    Video selectVideoWithoutStatus(Integer videoId);

    // 功能三：推荐视频（使用动态SQL构建器）
    @SelectProvider(type = VideoSqlProvider.class, method = "buildRecommendSql")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "duration", column = "duration")
    })
    List<RecommendVideoVO> selectRelatedRecommendVideos(
            @Param("videoId") Integer videoId,
            @Param("tags") List<String> tags,
            @Param("tagWeight") Double tagWeight,
            @Param("playWeight") Double playWeight,
            @Param("limit") Integer limit
    );

    // 默认热门推荐
    @Select("SELECT video_id, cover, author_id, author_name, title, duration, upload_time " +
            "FROM videos WHERE status = 'published' " +
            "ORDER BY view_count DESC LIMIT #{limit}")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "authorId", column = "author_id")
    })
    List<RecommendVideoVO> selectHotVideos(Integer limit);


    @SelectProvider(type = VideoSqlProvider.class, method = "buildSearchSql")
    @Results(id = "videoSearchMap", value = {
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "cover", column = "cover"),
            @Result(property = "title", column = "title"),
            @Result(property = "authorId", column = "author_id"),
            @Result(property = "authorName", column = "author_name"),
            @Result(property = "uploadTime", column = "upload_time"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "viewCount", column = "view_count")
    })
    IPage<VideoSearchVO> searchPublishedVideos(
            @Param("page") Page<Video> page,
            @Param("keyword") String keyword,
            @Param("sortField") String sortField,  // 新增排序字段参数
            @Param("sortOrder") String sortOrder   // 新增排序方向参数
    );
}
