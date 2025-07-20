package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("users")  // 对应数据库中的表名
public class User {

    @TableId(value = "user_id", type = IdType.AUTO)  // 主键
    private Integer userId;

    @TableField("user_name")  // 对应数据库中的字段
    private String userName;

    @TableField("password")
    private String password;

    @TableField("avatar")
    private String avatar;

    @TableField("email")
    private String email;

    @TableField("birthday")
    private String birthday;

    @TableField("gender")
    private String gender;

    @TableField("signature")
    private String signature;

    @TableField("announcement")
    private String announcement;

    @TableField("follow_count")
    private Integer followCount;

    @TableField("fan_count")
    private Integer fanCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("view_count")
    private Integer viewCount;


// Getters and Setters

}
