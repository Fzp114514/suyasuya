package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CollectedVideoVO {
    private Integer videoId;
    private String title;
    private String cover;
    private Integer duration;
    private Date collectTime;
}
