package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MasterpieceVO {
    private Integer videoId;
    private String cover;
    private Integer duration;
    private String title;
    private Integer viewCount;
    private Date uploadTime;
    private String introduction;
}
