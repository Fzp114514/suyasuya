package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 自定义查询方法
    // 根据邮箱查询用户
    User selectByEmail(String email);

    // 根据用户ID查询用户
    User selectByUserId(Integer userId);

    void incrementViewCount(Integer userId);
}
