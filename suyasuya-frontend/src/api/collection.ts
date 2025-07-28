// 此处存放所有视频收藏相关接口

import apiRequest from "@/utils/request"

export const createCollection = (collectionName: string, description: string) => {
    return apiRequest.post(`/collection/create`, null, {
        params: {
            collectionName,
            description
        }
    })
}

export const getUserCollections = () => {
    return apiRequest.get("/collection/user")
}

export const addVideoToCollection = (collectionId: number, videoId: number) =>
    apiRequest.post(`/collection/addVideo?collectionId=${collectionId}&videoId=${videoId}`)

export const checkVideoCollections = (videoId: number) =>
    apiRequest.get(`/collection/check?videoId=${videoId}`)

export const removeVideoFromCollection = (collectionId: number, videoId: number) =>
    apiRequest.delete(`/collection/deleteVideo?collectionId=${collectionId}&videoId=${videoId}`)

export const getRecentCollectedVideos = (userId: number, n: number) =>
    apiRequest.get(`/collection/getRecentVideos?userId=${userId}&n=${n}`)

export const getCollectionVideos = (collectionId: number, pageNum: number, pageSize: number) =>
    apiRequest.get(`/collection/getVideos/${collectionId}?pageNum=${pageNum}&pageSize=${pageSize}`)

export const updateCollection = (requestData: Object) =>
    apiRequest.post(`/collection/update`, requestData)

export const deleteCollection = (collectionId: number) =>
    apiRequest.delete(`/collection/deleteCollection/${collectionId}`)
