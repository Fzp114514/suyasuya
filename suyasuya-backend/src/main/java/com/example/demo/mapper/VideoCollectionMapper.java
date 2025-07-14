package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.VideoCollection;
import com.example.demo.vo.CollectedVideoVO;
import com.example.demo.vo.CollectionVideoVO;
import com.example.demo.vo.UserCollectionVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoCollectionMapper extends BaseMapper<VideoCollection> {
    @Select("SELECT c.collection_id AS collectionId, " +
            "c.collection_name AS collectionName, " +
            "c.is_default AS isDefault, " +
            "vc.collect_time AS collectTime " +
            "FROM collections c " +
            "INNER JOIN video_collections vc " +
            "ON c.collection_id = vc.collection_id " +
            "WHERE c.user_id = #{userId} AND vc.video_id = #{videoId} " +
            "ORDER BY vc.collect_time DESC")
    List<UserCollectionVO> selectCollectionsByUserAndVideo(
            @Param("userId") Integer userId,
            @Param("videoId") Integer videoId
    );

    @Select("SELECT v.video_id AS videoId, v.title , " +
            "v.cover , v.duration, vc.collect_time AS collectTime " +
            "FROM video_collections vc " +
            "INNER JOIN collections c ON vc.collection_id = c.collection_id " + // 新增关联
            "INNER JOIN videos v ON vc.video_id = v.video_id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY vc.collect_time DESC " +
            "LIMIT #{limit}")
    List<CollectedVideoVO> selectRecentCollectedVideos(@Param("userId") Integer userId,
                                                       @Param("limit") Integer limit);

    @Select("SELECT v.video_id AS videoId, v.title, " +
            "v.cover, v.duration, vc.collect_time AS collectTime " +
            "FROM video_collections vc " +
            "INNER JOIN videos v ON vc.video_id = v.video_id " +
            "WHERE vc.collection_id = #{collectionId} " +
            "ORDER BY vc.collect_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<CollectionVideoVO> selectVideosPage(@Param("collectionId") Integer collectionId,
                                             @Param("offset") Long offset,
                                             @Param("pageSize") Long pageSize);

    @Select("SELECT COUNT(*) FROM video_collections WHERE collection_id = #{collectionId}")
    Long selectVideoCount(@Param("collectionId") Integer collectionId);

    // 根据收藏夹ID删除所有关联视频
    @Delete("DELETE FROM video_collections WHERE collection_id = #{collectionId}")
    int deleteByCollectionId(@Param("collectionId") Integer collectionId);
}
