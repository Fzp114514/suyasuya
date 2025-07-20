package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@TableName("search_history")
public class SearchHistory {
    @TableId(value = "search_id", type = IdType.AUTO)
    private Integer searchId;

    @TableField("user_id")
    private Integer userId;

    @TableField("keyword")
    private String keyword;

    @TableField("search_time")
    private Date searchTime;

    // Getters and Setters...

}
