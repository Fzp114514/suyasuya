package com.example.demo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class WatchHistoryVO {
    private Integer videoId;
    private String cover;
    private String title;
    private Integer authorId;
    private String authorName;
    private Date watchTime;
    private Integer duration;
    private Integer viewCount;
}