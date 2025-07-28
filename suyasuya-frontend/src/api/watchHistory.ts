// 此处存放所有用户观看历史相关接口
import apiRequest from "@/utils/request";

export const recordWatchHistory = (videoId: number) =>
    apiRequest.post(`/history/recordWatchHistory?videoId=${videoId}`)

export const getWatchHistory = (pageNum: number, pageSize: number) =>
    apiRequest.get(`/history/getWatchHistory?page=${pageNum}&size=${pageSize}`)

export const deleteWatchHistory = (videoId: number) =>
    apiRequest.delete(`/history/delete?videoId=${videoId}`)

export const clearHistory = () =>
    apiRequest.delete(`/history/clear`)