package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// VideoCollection.java
@Setter
@Getter
@TableName("video_collections")
public class VideoCollection {
    @TableField("collection_id")
    private Integer collectionId;

    @TableField("video_id")
    private Integer videoId;

    @TableField("collect_time")
    private Date collectTime;


    // Getters and Setters...

}