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
@TableName("comments")
public class Comment {
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    @TableField("video_id")
    private Integer videoId;

    @TableField("user_id")
    private Integer userId;

    @TableField("content")
    private String content;

    @TableField("comment_time")
    private Date commentTime;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("reply_count")
    private Integer replyCount;

    // Getters and Setters...

}
