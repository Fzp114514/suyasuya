export interface UserInfo {
    userId: number
    userName: string
    avatar: string
    birthday: string
    gender: 'Male' | 'Female' | 'Other'
    signature: string
    announcement: string
    followCount: number
    fanCount: number
    likeCount: number
    viewCount: number
}

export interface Masterpiece {
    videoId: number
    cover: string
    duration: number
    title: string
    viewCount: number
    uploadTime: string
    introduction: string
}

export interface PageResult<T> {
    record: T[]
    total: number
    page: number
    size: number
    current: number
}