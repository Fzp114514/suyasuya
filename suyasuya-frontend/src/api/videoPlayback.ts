// 此处存放所有视频播放模块相关接口
import apiRequest from "@/utils/request"

export const getVideoDetail = (videoId: number) =>
    apiRequest.get(`/videos/detail/${videoId}`)

export const getVideoTags = (videoId: number) =>
    apiRequest.get(`/videos/tags/${videoId}`)

export const getRelatedRecommendVideos = (videoId: number) =>
    apiRequest.get(`/videos/recommend/${videoId}`)

export const incrementVideoViews = (videoId: number) =>
    apiRequest.post(`/videos/views/${videoId}`)
