<script setup lang="ts">
defineOptions({
    name: 'Video'
})
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import { nextTick, onMounted, ref } from 'vue'
import VideoPlayer from '@/components/VideoPlayer.vue'
import PaletteBtn from '@/components/PaletteBtn.vue'
import VideoComment from '@/components/VideoComment.vue'
import CommentInputBox from '@/components/CommentInputBox.vue'
import ListVideoBox from '@/components/ListVideoBox.vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { addVideoToCollection, checkVideoCollections, createCollection, getUserCollections, removeVideoFromCollection } from '@/api/collection'
import { getRelatedRecommendVideos, getVideoDetail, getVideoTags } from '@/api/videoPlayback'
import { recordWatchHistory } from '@/api/watchHistory'
import { formatUploadTime, formatUploadTimeDetail, formatViewCounts, formatWrapText, getBaseUrl } from '@/main'

const route = useRoute()
const userStore = useUserStore()

const titleElement = ref<HTMLHeadElement | null>(null)                  // 获取Dom元素 titleElement
const isShowOverflowPanel = ref<boolean>(false)          // 标题隐藏面板显示状态
const isTitleOverLimit = ref<boolean | null>(null)              // 描述内容是否超过指定宽度

const checkTitleOverflow = () => {
    nextTick(() => {
        if (titleElement.value) {
            isTitleOverLimit.value = titleElement.value.scrollWidth > titleElement.value.offsetWidth
        }
    })
}

const descInfo = ref<HTMLDivElement | null>()                          // 获取Dom元素 descInfo
const isShowDetailDesc = ref<boolean>(false)             // 是否展开视频描述
const isDescOverLimit = ref<Boolean>(false)              // 描述内容是否超过指定高度
const toggleBtnText = ref<string>('展示更多')

const checkDescOverflow = () => {
    nextTick(() => {
        if (descInfo.value) {
            isDescOverLimit.value = descInfo.value.scrollHeight > 91
        }
    })
}

const showDetailDesc = () => {
    isShowDetailDesc.value = !isShowDetailDesc.value
    toggleBtnText.value = isShowDetailDesc.value ? '收起' : '展示更多'
}

// ========================= 收藏模块相关方法 ====================================

const isCollection = ref<boolean>(false)                            // 是否为收藏视频
const currentCollectionId = ref<number | null>(null)                // 视频所处收藏夹的id（若为收藏视频）

const collectionDialogVisible = ref<boolean>(false)                 // 收藏弹窗显示状态
const isCreateCollection = ref<boolean>(false)                      // 是否新增收藏夹
const newCollectionName = ref<string>('')                           // 新增收藏夹名字
const radio = ref<number>()                                         // 单选框默认值
interface CollectionItem {
    collectionId: number;
    collectionName: string;
}

const collectionsList = ref<CollectionItem[]>([
    {
        collectionId: 1,
        collectionName: '默认收藏夹'
    }
])                        // 收藏夹列表 
const checkedCollectionId = ref<number | null>(null)                // 选择的收藏夹id


// 查询视频是否被用户收藏   注：接口可支持查询多个收藏夹，但受限于前端目前仅安排一个视频存一个收藏夹
const checkVideo = async () => {
    const videoId = +route.params.videoId
    if (!videoId) {
        const res = await checkVideoCollections(videoId)
        console.log(res)
        if (res.data.length) {
            isCollection.value = true
            currentCollectionId.value = res.data[0].collectionId
        }
    }
}

// 开启收藏弹窗方法     注：此处代码待优化
const favouriteVideo = async () => {
    collectionDialogVisible.value = true
    const res = await getUserCollections()
    if (res.success) {
        collectionsList.value = res.data
        radio.value = 0
        checkedCollectionId.value = 1
        console.log(collectionsList.value)
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }
    // isCollection.value = true
}
// 选择收藏夹方法
const selectCollection = (collectionId: number) => {
    checkedCollectionId.value = collectionId
    console.log(checkedCollectionId.value)
}
// 创建新收藏夹方法
const createNewCollection = async () => {
    if (!newCollectionName.value.trim()) {
        ElMessage({
            message: '收藏夹名不能为空',
            type: 'error'
        })
        isCreateCollection.value = false
        return
    }
    const res = await createCollection(newCollectionName.value, '')
    if (res.success) {
        const res2 = await getUserCollections()
        if (res2.success) {
            collectionsList.value = res2.data
        }
        else {
            ElMessage({
                message: res2.message,
                type: 'error'
            })
            console.log(res)
        }
    }
}

// 插入视频到用户收藏夹方法
const addVideo = async () => {
    if (!checkedCollectionId.value) {
        ElMessage({
            message: '请选择收藏夹',
            type: 'error'
        })
        return
    }
    const videoId = +route.params.videoId
    const res = await addVideoToCollection(checkedCollectionId.value, videoId)
    console.log(res)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        videoInfo.value.collectionCount++
        currentCollectionId.value = checkedCollectionId.value
        collectionDialogVisible.value = false
        isCollection.value = true
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

// 移除收藏视频方法 
const unfavouriteVideo = async () => {
    if (!currentCollectionId.value) {
        ElMessage({
            message: '当前视频未收藏',
            type: 'error'
        })
        return
    }
    const videoId = +route.params.videoId
    const res = await removeVideoFromCollection(currentCollectionId.value, videoId)
    console.log(res)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        videoInfo.value.collectionCount--
        currentCollectionId.value = null
        isCollection.value = false
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }
}

// ========================= 点赞模块相关方法 =============================

const islike = ref(false)                       // 是否为点赞视频

const likeVideo = () => {
    // 注：此处应调用api将视频点赞信息存储进数据库
    islike.value = true
}
const unlikeVideo = () => {
    // 注：此处应调用api将取消视频点赞信息存储进数据库
    islike.value = false
}

const handleLike = () => islike.value ? unlikeVideo() : likeVideo()
const handleCollection = () => isCollection.value ? unfavouriteVideo() : favouriteVideo()



// ========================= 展示数据 ===================================

const isShowHotComments = ref(true)             // 是否展示最热评论, 值为false时为展示最新评论

// 播放视频信息
const videoInfo = ref({
    authorInfo: {
        authorId: -1,
        authorName: '',
        avatar: '',
        signature: ''
    },
    videoId: -1,
    video: '',
    introduction: '',
    cover: '',
    uploadTime: '',
    title: '',
    duration: 0,
    collectionCount: 0,
    likeCount: 0,
    viewCount: 0,
    commentCount: 0,
})

const videoTags = ref()             // 当前播放视频的所有标签
const recommendedList = ref()       // 相关视频推荐列表

// 测试数据 视频评论区内容
const commentContents = ref([
    {
        commentId: 1,
        userId: 114514,
        nickName: '旧时画',
        avatar: '../../../public/imgs/avatar/avatar1.jpg',
        commentContent: '大佬牛逼',
        commentTime: '2015-01-01 13:01',
        islike: false,
        likes: '50',
        replies: '100'
    }
])

const showHotComments = () => {
    if (isShowHotComments.value)
        return
    isShowHotComments.value = true
    // 注：此处调用api获取最热评论
}
const showNewComments = () => {
    if (!isShowHotComments.value)
        return
    isShowHotComments.value = false
    // 注：此处调用api获取最新评论
}

const videoId = +route.params.videoId

// 获取视频详细信息方法
const getVideoInfo = async () => {
    if (!videoId) return
    const res = await getVideoDetail(videoId)
    console.log(res)
    if (res.success) {
        videoInfo.value = res.data
        checkTitleOverflow()
        checkDescOverflow()
        const res2 = await recordWatchHistory(videoId)
        console.log(res2)
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}
// 获取当前视频的所有标签
const getCurrentVideoTags = async () => {
    if (!videoId) return
    const res = await getVideoTags(videoId)
    if (res.success) {
        videoTags.value = res.data
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }
}
// 获取相关视频推荐列表
const getRelatedVideoList = async () => {
    const res = await getRelatedRecommendVideos(videoId)
    console.log(res)
    if (res.success) {
        recommendedList.value = res.data
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

onMounted(() => {
    getVideoInfo()
    checkVideo()
    getCurrentVideoTags()
    getRelatedVideoList()
})

</script>
<template>
    <div class="bg">
        <Header></Header>
        <div class="body w">
            <div class="left-container">
                <div class="video-info">
                    <div class="video-info-title">
                        <h1 v-show="!isShowOverflowPanel" ref="titleElement" class="title">{{ videoInfo.title }}</h1>
                        <div v-if="isTitleOverLimit" class="show-more" @mouseenter="isShowOverflowPanel = true"
                            @mouseleave="isShowOverflowPanel = false">
                            <el-icon class="rotate-icon"><i-ep-ArrowDownBold /></el-icon>
                        </div>
                        <div v-show="isShowOverflowPanel" class="overflow-panel">
                            <h1 class="title">{{ videoInfo.title }}</h1>
                            <div class="video-detail-info">
                                <div class="viewCounts">
                                    <div class="icon"><el-icon><i-ep-VideoPlay /></el-icon></div>
                                    <span>{{ videoInfo.viewCount }}</span>
                                </div>
                                <div class="creativeTime">{{ formatUploadTimeDetail(+videoInfo.uploadTime) }}</div>
                            </div>
                        </div>
                    </div>
                    <div v-show="!isShowOverflowPanel" class="video-detail-info">
                        <div class="viewCounts">
                            <div class="icon"><el-icon><i-ep-VideoPlay /></el-icon></div>
                            <span>{{ videoInfo.viewCount }}</span>
                        </div>
                        <div class="creativeTime">{{ formatUploadTimeDetail(+videoInfo.uploadTime) }}</div>
                    </div>
                </div>
                <VideoPlayer :videoUrl="videoInfo.video" :videoId="videoInfo.videoId"></VideoPlayer>
                <div class="video-toolBar">
                    <div class="video-toolBar-left">
                        <div class="toolBar-btn" @click="handleLike" title="点赞">
                            <span v-show="!islike" class="toolBar-icon like"></span>
                            <span v-show="islike" class="toolBar-icon like-selected"></span>
                            <span :class="{ active: islike }">{{ videoInfo.likeCount }}</span>
                        </div>
                        <div class="toolBar-btn" @click="handleCollection" title="收藏">
                            <span v-show="!isCollection" class="toolBar-icon collection"></span>
                            <span v-show="isCollection" class="toolBar-icon collection-selected"></span>
                            <span :class="{ active: isCollection }">{{ videoInfo.collectionCount }}</span>
                        </div>
                        <div class="toolBar-btn">
                            <span class="toolBar-icon share"></span>
                            <span>分享</span>
                        </div>
                    </div>
                    <div class="video-toolBar-right">
                        <div class="toolBar-btn">
                            <span class="toolBar-icon more"></span>
                        </div>
                    </div>
                </div>
                <div v-if="videoInfo.introduction.length" class="video-desc-container">
                    <div ref="descInfo"
                        :style="{ height: isDescOverLimit ? (isShowDetailDesc ? 'auto' : '91px') : 'auto' }"
                        v-html="formatWrapText(videoInfo.introduction)" class="base-desc-info">
                    </div>
                    <div v-if="isDescOverLimit" class="toggle-btn">
                        <span class="toggle-btn-text" @click="showDetailDesc">{{ toggleBtnText }}</span>
                    </div>
                </div>
                <div class="video-tag-container">
                    <div v-for="item of videoTags">
                        <div class="tag">
                            <el-tag type="primary">{{ item }}</el-tag>
                        </div>
                    </div>
                </div>
                <div class="comment-header">
                    <div class="navbar">
                        <div class="title">
                            <h2>评论</h2>
                            <div class="count">90</div>
                        </div>
                        <div class="sort-action">
                            <button @click="showHotComments" :class="{ active: isShowHotComments }">最热</button>
                            <span class="divide">|</span>
                            <button @click="showNewComments" :class="{ active: !isShowHotComments }">最新</button>
                        </div>
                    </div>
                    <CommentInputBox></CommentInputBox>
                </div>
                <div class="comment-contents">
                    <VideoComment :commentContents="commentContents"></VideoComment>
                </div>
            </div>
            <div class="right-container">
                <div class="up-info-container">
                    <div class="up-info-left">
                        <div class="avatar">
                            <a :href="`/space/${videoInfo.authorInfo.authorId}`" target="_blank">
                                <img :src="`${getBaseUrl()}/avatar/${videoInfo.authorInfo.avatar}`" alt="">
                            </a>
                        </div>
                    </div>
                    <div class="up-info-right">
                        <div class="up-info-detail">
                            <div class="nickName">
                                <a :href="`/space/${userStore.getUserId()}`" target="_blank">{{
                                    videoInfo.authorInfo.authorName }}</a>
                            </div>
                            <div class="up-info-sign">{{ videoInfo.authorInfo.signature }}</div>
                        </div>
                        <div class="up-info-btn-panel">
                            <div class="sub">关注</div>
                        </div>
                    </div>
                </div>
                <div class="recommend-video">
                    <div class="header">相关推荐</div>
                    <ListVideoBox :videos-msg="recommendedList">
                        <template #videoInfo="{ video }">
                            <div class="videoInfo">
                                <h3 class="title" :title="video.title">
                                    <a :href="`/video/${video.videoId}`" target="_blank">
                                        {{ video.title }}
                                    </a>
                                </h3>
                                <div class="upname">
                                    <a :href="`/space/${video.authorId}`" class="author" target="_blank">
                                        {{ video.authorName }}
                                    </a>
                                </div>
                                <div class="playInfo">
                                    <div class="viewCounts">
                                        <div class="icon"><el-icon><i-ep-VideoPlay /></el-icon></div>
                                        <span>{{ formatViewCounts(video.viewCount) }}</span>
                                    </div>
                                    <div class="creativeTime">
                                        {{ formatUploadTime(video.uploadTime) }}
                                    </div>
                                </div>
                            </div>
                        </template>
                    </ListVideoBox>
                </div>
            </div>
        </div>
    </div>
    <el-dialog v-model="collectionDialogVisible" title="添加到收藏夹" width="400" center>
        <div class="content">
            <div class="collectionsList">
                <ul>
                    <li v-for="(item, index) in collectionsList" :key="index">
                        <input v-model="radio" @click="selectCollection(item.collectionId)" type="radio"
                            name="collection" :id="String(index)" class="checkBtn" :value="index">
                        <label :for="String(index)" class="label">
                            {{ item.collectionName }}</label>
                    </li>
                </ul>
            </div>
            <div class="container">
                <input v-model="newCollectionName" @focus="isCreateCollection = true" placeholder="新建收藏夹"
                    class="createCollections" type="text" @keyup.enter="createNewCollection" />
                <button v-show="isCreateCollection" @click="createNewCollection" class="createBtn">确定</button>
            </div>
        </div>
        <template #footer>

            <div class="dialog-footer">
                <el-button @click="collectionDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addVideo">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
    <PaletteBtn></PaletteBtn>
</template>
<style scoped>
@import url(../assets/css/views/video.css);
</style>