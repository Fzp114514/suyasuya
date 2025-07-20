package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("video_categories")
public class VideoCategory {
    // Getters and Setters...
    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    @TableField("category_name")
    private String categoryName;

    @TableField("description")
    private String description;

}
