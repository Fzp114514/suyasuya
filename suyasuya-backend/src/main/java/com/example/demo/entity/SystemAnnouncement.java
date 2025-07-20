package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// Announcement.java
@Setter
@Getter
@TableName("system_announcements")
public class SystemAnnouncement {
    @TableId(value = "announcement_id", type = IdType.AUTO)
    private Integer announcementId;

    @TableField("publish_time")
    private Date publishTime;

    @TableField("publisher_id")
    private Integer publisherId;

    @TableField("content")
    private String content;

    // Getters and Setters...

}