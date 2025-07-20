package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// Follow.java
@Setter
@Getter
@TableName("follows")
public class Follow {
    @TableId(value = "follow_id", type = IdType.AUTO)
    private Integer followId;

    @TableField("follower_id")
    private Integer followerId;

    @TableField("following_id")
    private Integer followingId;

    @TableField("follow_time")
    private Date followTime;

    // Getters and Setters...

}