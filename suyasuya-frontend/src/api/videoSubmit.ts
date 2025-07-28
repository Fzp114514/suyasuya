// 此处存放所有视频投稿模块相关接口
import { AxiosProgressEvent } from "axios"
import apiRequest from "@/utils/request"

type ProgressCallback = (percentage: number) => void

export const uploadTemporarilyVideo = (formData: Object, onProgress: ProgressCallback) =>
    apiRequest.post(`/upload/tempVideo`, formData, {
        timeout: 60000,                         // 接口响应时长暂定为一分钟，实际情况应根据视频大小和网络状况进行调整
        onUploadProgress: (e: AxiosProgressEvent) => {
            if (e.total) {
                const percent = Math.round((e.loaded / e.total) * 100)
                onProgress(percent)             // 通过回调函数传递上传进度
            }
        }
    })

export const publishVideo = (formData: FormData) =>
    apiRequest.post(`/upload/publish`, formData)

