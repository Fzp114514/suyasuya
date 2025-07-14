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
    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(String email);

    // 根据用户ID查询用户
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User selectByUserId(Integer userId);

    @Update("UPDATE users SET view_count = view_count + 1 WHERE user_id = #{userId}")
    void incrementViewCount(Integer userId);
}
