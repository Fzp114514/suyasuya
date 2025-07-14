package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("video_tags")
public class VideoTag {
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Integer tagId;

    @TableField("tag_name")
    private String tagName;

    @TableField("related_count")
    private Integer relatedCount;

    // Getters and Setters...
    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getRelatedCount() {
        return relatedCount;
    }

    public void setRelatedCount(Integer relatedCount) {
        this.relatedCount = relatedCount;
    }
}