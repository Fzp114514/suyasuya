package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

// 视频详情VO
@Data
public class VideoDetailVO {
    private Integer videoId;
    private String video;
    private Date uploadTime;
    private String title;
    private String cover;
    private Integer duration;
    private String introduction;
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
    private Integer collectionCount;
    private AuthorSimpleVO authorInfo;
}