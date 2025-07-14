package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class VideoSearchVO {
    private Integer videoId;
    private String cover;
    private String title;
    private Integer authorId;
    private String authorName;
    private Date uploadTime;
    private Integer duration;
    private Integer viewCount;
    private Integer CollectionCount; // 收藏数

}