package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CollectionVideoVO {
    private Integer videoId;
    private String title;
    private String cover;
    private Integer duration;
    private Date collectTime;
}
