package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.VideoCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VideoCategoryMapper extends BaseMapper<VideoCategory> {

    boolean existsById(Integer categoryId);
}
