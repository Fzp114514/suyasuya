// 此处存放所有用户代表作相关接口

import request from "@/utils/request"

export const getMasterpiece = userId =>
    request.get(`/getMasterpiece?userId=${userId}`)

export const updateMasterpiece = videoId =>
    request.post(`/setMasterpiece?videoId=${videoId}`)