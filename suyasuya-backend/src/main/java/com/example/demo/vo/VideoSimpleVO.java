package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

// VideoSimpleVO.java 视图对象
@Data
public class VideoSimpleVO {
    private Integer videoId;
    private Integer authorId;
    private String authorName;
    private Date uploadTime;
    private String title;
    private Integer duration;
    private String cover;
    private Integer viewCount;
}