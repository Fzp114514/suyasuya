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

    List<UserCollectionVO> selectCollectionsByUserAndVideo(
            @Param("userId") Integer userId,
            @Param("videoId") Integer videoId
    );

    List<CollectedVideoVO> selectRecentCollectedVideos(
            @Param("userId") Integer userId,
            @Param("limit") Integer limit
    );

    List<CollectionVideoVO> selectVideosPage(
            @Param("collectionId") Integer collectionId,
            @Param("offset") Long offset,
            @Param("pageSize") Long pageSize
    );

    Long selectVideoCount(@Param("collectionId") Integer collectionId);

    int deleteByCollectionId(@Param("collectionId") Integer collectionId);
}
