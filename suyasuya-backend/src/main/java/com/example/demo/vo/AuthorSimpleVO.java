package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

// 作者简略信息VO
@Data
@AllArgsConstructor
public class AuthorSimpleVO {
    private Integer authorId;
    private String authorName;
    private String avatar;
    private String signature;
}