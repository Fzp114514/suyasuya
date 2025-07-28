export interface VideoInfo {
    videoId: number
    title: string
    cover: string
    duration: number
    uploadTime: string
    viewCount: number
    autherId?: string
    autherName?: string
    collectTime?: string // 收藏时间
}

interface AuthorInfo {
    authorId: number | string
    authorName: string
    avatar: string
    signature: string
}

interface Comment {
    commentId: number
    userId: number
    nickName: string
    avatar: string
    commentContent: string
    commentTime: string
    islike: boolean
    likes: number | string
    replies: number | string
}

interface Collection {
    collectionId: number | string
    collectionName: string
}