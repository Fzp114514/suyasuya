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

    void updateAuthorNameByUserId(
            @Param("userId") Integer userId,
            @Param("newAuthorName") String newAuthorName);

    boolean existsByUserName(String userName);

    Integer selectAuthorIdByVideoId(Integer videoId);

    int incrementViewCount(Integer videoId);

    int updateCollectionCount(
            @Param("videoId") Integer videoId,
            @Param("increment") int increment);

    List<VideoSimpleVO> selectRandomVideos(Integer count);

    List<VideoSimpleVO> selectRandomVideosByCategory(
            @Param("categoryId") Integer categoryId,
            @Param("count") Integer count);

    Integer countPublishedVideos();

    Integer countPublishedVideosByCategory(Integer categoryId);

    Video selectVideoWithoutStatus(Integer videoId);

    List<RecommendVideoVO> selectRelatedRecommendVideos(
            @Param("videoId") Integer videoId,
            @Param("tags") List<String> tags,
            @Param("tagWeight") Double tagWeight,
            @Param("playWeight") Double playWeight,
            @Param("limit") Integer limit);

    List<RecommendVideoVO> selectHotVideos(Integer limit);

    IPage<VideoSearchVO> searchPublishedVideos(
            @Param("page") Page<Video> page,
            @Param("keyword") String keyword,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);
}
