package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("video_tag_relations")
public class VideoTagRelation {
    @TableField("video_id")
    private Integer videoId;

    @TableField("tag_id")
    private Integer tagId;

    // Getters and Setters...

}