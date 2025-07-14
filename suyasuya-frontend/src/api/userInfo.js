// 此处存放所有用户信息模块相关接口

import request from "@/utils/request";

export const getUserInfo = () =>
    request.get(`/getUserInfo`)

export const getOtherUserInfo = userId =>
    request.get(`/getOtherUserInfo?userId=${userId}`)

export const updateSignature = newSignature =>
    request.post(`/updateSignature?newSignature=${newSignature}`)

export const updateAnnouncement = newAnnouncement =>
    request.post(`/updateAnnouncement?newAnnouncement=${newAnnouncement}`)

export const updateUserInfo = userInfo =>
    request.put(`/updateUserInfo`, userInfo)

export const updateAvatar = formData =>
    request.post(`/upload/avatar`, formData)