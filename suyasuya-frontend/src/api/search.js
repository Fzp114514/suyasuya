// 此处存放所有搜索模块相关接口

import request from "@/utils/request";

export const searchVideosByKeyword = (keyword, pageNum, pageSize, sortType) =>
    request.get(`/search/videos?keyword=${keyword}&pageNum=${pageNum}&pageSize=${pageSize}&sortType=${sortType}`)
