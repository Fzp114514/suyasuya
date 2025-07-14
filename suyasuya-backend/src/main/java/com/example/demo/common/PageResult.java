package com.example.demo.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;       // 当前页数据列表
    private Long total;         // 总记录数
    private Long pageNum;       // 当前页面
    private Long pageSize;      // 每页大小
    private Long pages;         // 总页数

    public PageResult(List<T> list, Long total, Long pageNum, Long pageSize, Long pages) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
    }
}