package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

// 推荐视频VO
@Data
public class RecommendVideoVO {
    private Integer videoId;
    private String cover;
    private Integer authorId;
    private String authorName;
    private String title;
    private Date uploadTime;
    private Integer duration;
    private Integer viewCount;
}
