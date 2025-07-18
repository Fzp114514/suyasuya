// 此处存放所有视频列表请求相关接口

import request from "@/utils/request";

export const getRandomVideos = count =>
    request.get(`/videos/random?count=${count}`)

export const getCategoryVideos = (categoryId, count) =>
    request.get(`/videos/category/${categoryId}?count=${count}`)

export const getVideosByUserByUploadTime = (userId, pageNum, pageSize) =>
    request.post(`/videos/getVideosByUserByUploadTime?userId=${userId}&pageNum=${pageNum}&pageSize=${pageSize}`)

export const getVideosByUserByViewCount = (userId, pageNum, pageSize) =>
    request.post(`/videos/getVideosByUserByViewCount?userId=${userId}&pageNum=${pageNum}&pageSize=${pageSize}`)

export const getVideosByUserByCollectionCount = (userId, pageNum, pageSize) =>
    request.post(`/videos/getVideosByUserByCollectionCount?userId=${userId}&pageNum=${pageNum}&pageSize=${pageSize}`)