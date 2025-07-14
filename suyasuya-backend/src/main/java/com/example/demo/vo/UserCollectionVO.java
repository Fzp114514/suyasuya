package com.example.demo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserCollectionVO {
    private Integer collectionId;
    private String collectionName;
    private Boolean isDefault;
    private Date collectTime;
}
