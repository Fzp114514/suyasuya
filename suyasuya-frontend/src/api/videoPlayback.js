// 此处存放所有视频播放模块相关接口

import request from "@/utils/request";

export const getVideoDetail = videoId =>
    request.get(`/videos/detail/${videoId}`)

export const getVideoTags = videoId =>
    request.get(`/videos/tags/${videoId}`)

export const getRelatedRecommendVideos = videoId =>
    request.get(`/videos/recommend/${videoId}`)

export const incrementVideoViews = videoId =>
    request.post(`/videos/views/${videoId}`)
