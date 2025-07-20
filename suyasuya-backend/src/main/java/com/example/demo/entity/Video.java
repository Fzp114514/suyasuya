package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@TableName("videos")  // 对应数据库中的表名
public class Video {

    @TableId(value = "video_id", type = IdType.AUTO)  // 主键，自动生成
    private Integer videoId;

    @TableField(value = "video")
    private String video;

    @TableField(value = "status")
    private String status;

    @TableField("author_id")  // 对应数据库中的字段
    private Integer authorId;

    @TableField("author_name")
    private String authorName;

    @TableField("upload_time")
    private Date uploadTime;

    @TableField("duration")
    private Integer duration;

    @TableField("title")
    private String title;

    @TableField("cover")
    private String cover;

    @TableField("introduction")
    private String introduction;

    @TableField("category_id")
    private Integer categoryId;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("collection_count")
    private Integer collectionCount;

    // Getters and Setters...

    // Getters 和 Setters 省略，可使用 Lombok 自动生成
}
