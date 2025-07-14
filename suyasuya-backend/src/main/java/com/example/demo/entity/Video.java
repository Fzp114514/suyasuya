package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@TableName("videos")  // 对应数据库中的表名
public class Video {

    @TableId(value = "video_id", type = IdType.AUTO)  // 主键，自动生成
    private Integer videoId;

    @TableField(value = "video")
    private String video;

    @TableField(value = "status")
    private String status;

    @TableField("author_id")  // 对应数据库中的字段
    private Integer authorId;

    @TableField("author_name")
    private String authorName;

    @TableField("upload_time")
    private Date uploadTime;

    @TableField("duration")
    private Integer duration;

    @TableField("title")
    private String title;

    @TableField("cover")
    private String cover;

    @TableField("introduction")
    private String introduction;

    @TableField("category_id")
    private Integer categoryId;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("collection_count")
    private Integer collectionCount;

    // Getters and Setters...

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(Integer collectionCount) {
        this.collectionCount = collectionCount;
    }

    // Getters 和 Setters 省略，可使用 Lombok 自动生成
}
