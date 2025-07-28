// 此处存放所有搜索模块相关接口
import apiRequest from "@/utils/request"

export const searchVideosByKeyword = (keyword: string, pageNum: number, pageSize: number, sortType: string) =>
    apiRequest.get(`/search/videos?keyword=${keyword}&pageNum=${pageNum}&pageSize=${pageSize}&sortType=${sortType}`)
