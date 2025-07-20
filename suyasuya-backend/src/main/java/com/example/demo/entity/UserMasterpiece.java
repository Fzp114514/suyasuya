package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("user_masterpieces")
public class UserMasterpiece {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @TableField("video_id")
    private Integer videoId;

    // Getters and Setters...

}