package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.VideoTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VideoTagRelationMapper extends BaseMapper<VideoTagRelation> {

    boolean existsRelation(@Param("videoId") Integer videoId, @Param("tagId") Integer tagId);
}
