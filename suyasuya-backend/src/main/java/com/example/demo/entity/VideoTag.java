package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("video_tags")
public class VideoTag {
    // Getters and Setters...
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Integer tagId;

    @TableField("tag_name")
    private String tagName;

    @TableField("related_count")
    private Integer relatedCount;

}