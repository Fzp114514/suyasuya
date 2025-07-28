// 此处存放所有用户信息模块相关接口
import apiRequest from "@/utils/request"

export const getUserInfo = () =>
    apiRequest.get(`/getUserInfo`)

export const getOtherUserInfo = (userId: number) =>
    apiRequest.get(`/getOtherUserInfo?userId=${userId}`)

export const updateSignature = (newSignature: string) =>
    apiRequest.post(`/updateSignature?newSignature=${newSignature}`)

export const updateAnnouncement = (newAnnouncement: string) =>
    apiRequest.post(`/updateAnnouncement?newAnnouncement=${newAnnouncement}`)

export const updateUserInfo = (userInfo: Object) =>
    apiRequest.put(`/updateUserInfo`, userInfo)

export const updateAvatar = (formData: FormData) =>
    apiRequest.post(`/upload/avatar`, formData)