package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VideoTagMapper extends BaseMapper<VideoTag> {

    VideoTag selectByName(@Param("tagName") String tagName);

    List<String> selectTagsByVideoId(@Param("videoId") Integer videoId);

    void incrementRelatedCount(@Param("tagId") Integer tagId);
}
