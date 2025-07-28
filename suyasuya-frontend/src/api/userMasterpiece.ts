// 此处存放所有用户代表作相关接口

import apiRequest from "@/utils/request"

export const getMasterpiece = (userId: number) =>
    apiRequest.get(`/getMasterpiece?userId=${userId}`)

export const updateMasterpiece = (videoId: number) =>
    apiRequest.post(`/setMasterpiece?videoId=${videoId}`)