// 此处存放所有视频收藏相关接口

import request from "@/utils/request";

export const createCollection = (collectionName, description) =>
    request.post(`/collection/create?collectionName=${collectionName}&description=${description}`)

export const getUserCollections = () =>
    request.get(`/collection/user`)

export const addVideoToCollection = (collectionId, videoId) =>
    request.post(`/collection/addVideo?collectionId=${collectionId}&videoId=${videoId}`)

export const checkVideoCollections = videoId =>
    request.get(`/collection/check?videoId=${videoId}`)

export const removeVideoFromCollection = (collectionId, videoId) =>
    request.delete(`/collection/deleteVideo?collectionId=${collectionId}&videoId=${videoId}`)

export const getRecentCollectedVideos = (userId, n) =>
    request.get(`/collection/getRecentVideos?userId=${userId}&n=${n}`)

export const getCollectionVideos = (collectionId, pageNum, pageSize) =>
    request.get(`/collection/getVideos/${collectionId}?pageNum=${pageNum}&pageSize=${pageSize}`)

export const updateCollection = requestData =>
    request.post(`/collection/update`, requestData)

export const deleteCollection = collectionId =>
    request.delete(`/collection/deleteCollection/${collectionId}`)
