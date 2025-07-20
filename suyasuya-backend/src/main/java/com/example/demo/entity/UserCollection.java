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
@TableName("user_collections")
public class UserCollection {
    @TableId(value = "collection_id", type = IdType.AUTO)
    private Integer collectionId;

    @TableField("user_id")
    private Integer userId;

    @TableField("collection_name")
    private String collectionName;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
    @TableField("is_default")
    private Boolean isDefault;
    @TableField("video_count")
    private Integer videoCount;

    // Getters and Setters...

}