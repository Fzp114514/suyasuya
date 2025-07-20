package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// WatchHistory.java
@Setter
@Getter
@TableName("watch_history")
public class WatchHistory {
    @TableId(value = "history_id", type = IdType.AUTO)
    private Integer historyId;

    @TableField("user_id")
    private Integer userId;

    @TableField("video_id")
    private Integer videoId;

    @TableField("watch_time")
    private Date watchTime;

    // Getters and Setters...

}