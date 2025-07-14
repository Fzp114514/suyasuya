// 此处存放所有用户观看历史相关接口

import request from "@/utils/request";

export const recordWatchHistory = videoId =>
    request.post(`/history/recordWatchHistory?videoId=${videoId}`)

export const getWatchHistory = (pageNum, pageSize) =>
    request.get(`/history/getWatchHistory?page=${pageNum}&size=${pageSize}`)

export const deleteWatchHistory = videoId =>
    request.delete(`/history/delete?videoId=${videoId}`)

export const clearHistory = () =>
    request.delete(`/history/clear`)