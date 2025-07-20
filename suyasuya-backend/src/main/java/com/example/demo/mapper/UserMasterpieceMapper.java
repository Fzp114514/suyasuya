package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserMasterpiece;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMasterpieceMapper extends BaseMapper<UserMasterpiece> {
    // 使用ON DUPLICATE KEY UPDATE语法实现原子操作
    int upsert(@Param("userId") Integer userId, @Param("videoId") Integer videoId);
}
