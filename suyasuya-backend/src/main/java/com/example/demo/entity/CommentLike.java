package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("comment_likes")
public class CommentLike {
    @TableId(value = "like_id", type = IdType.AUTO)
    private Integer likeId;

    @TableField("user_id")
    private Integer userId;

    @TableField("comment_id")
    private Integer commentId;

    // Getters and Setters...

}
