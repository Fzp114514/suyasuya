package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@TableName("video_likes")
public class VideoLike {
    @TableId(value = "like_id", type = IdType.AUTO)
    private Integer likeId;

    @TableField("user_id")
    private Integer userId;

    @TableField("video_id")
    private Integer videoId;

    @TableField("like_time")
    private Date likeTime;

    @TableField("status")
    private Integer status;

    // Getters and Setters...

}
