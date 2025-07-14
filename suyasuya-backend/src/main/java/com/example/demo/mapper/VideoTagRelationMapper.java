package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.VideoTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VideoTagRelationMapper extends BaseMapper<VideoTagRelation> {
    @Select("SELECT COUNT(*) FROM video_tag_relations WHERE video_id = #{videoId} AND tag_id = #{tagId}")
    boolean existsRelation(@Param("videoId") Integer videoId, @Param("tagId") Integer tagId);
}
