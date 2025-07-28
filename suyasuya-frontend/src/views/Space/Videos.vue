<script setup lang="ts">
defineOptions({
    name: 'Videos'
})
import SmallVideoBox from '@/components/SmallVideoBox.vue'
import { onMounted, ref } from 'vue'
import ListVideoBox from '@/components/ListVideoBox.vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useDisplayStore } from '@/stores/display'
import { getVideosByUserByCollectionCount, getVideosByUserByUploadTime, getVideosByUserByViewCount } from '@/api/videoQuery'
import { formatUploadTime, formatViewCounts, formatWrapText } from '@/main'

const displayStore = useDisplayStore()
const route = useRoute()
const props = defineProps({
    isMe: Boolean
})

const showVideos = ref([])

// 分页组件相关信息
const currentPage = ref<number>(1)  // 当前页数
const pages = ref<number>(1)        // 总页数
const size = ref<number>(18)        // 每页的视频个数
const total = ref<number>(1)        // 总视频个数

// 所有视频类型
const videosType = ref<string[]>(['最新发布', '最多播放', '最多收藏'])
const selectedType = ref<string>('')
const changeVideosSortord = (type: string = selectedType.value) => {
    if (selectedType.value === type)
        return
    selectedType.value = type
    getSubVideos(type)
}

const getSubVideos = async (videosType: string) => {
    let res = null
    const userId = +route.params.userId
    try {
        switch (videosType) {
            case '最新发布':
                res = await getVideosByUserByUploadTime(userId, currentPage.value, size.value)
                break
            case '最多播放':
                res = await getVideosByUserByViewCount(userId, currentPage.value, size.value)
                break
            case '最多收藏':
                res = await getVideosByUserByCollectionCount(userId, currentPage.value, size.value)
                break
            default:
                break
        }
        if (!res) {
            throw new Error('获取视频失败')
        }
        if (res.success) {
            console.log(res.data)
            showVideos.value = res.data.records
            pages.value = res.data.pages
            total.value = res.data.total
        } else {
            throw new Error(res.message || '获取视频失败')
        }
    } catch (error: any) {
        ElMessage({
            message: error.message || '获取视频失败',
            type: 'error'
        })
        console.log(error)
    }
}

const handelCurrentChange = (page: number) => {
    currentPage.value = page
    getSubVideos(selectedType.value)
}

onMounted(() => {
    changeVideosSortord(videosType.value[0])
})

</script>
<template>
    <div class="videos-bg">
        <div class="head">
            <div class="head-left">
                <h3 v-if="isMe" class="title">我的视频</h3>
                <h3 v-else class="title">TA的视频</h3>
                <ul class="options">
                    <li v-for="(type, index) in videosType" :key="index" :class="{ active: selectedType === type }"
                        @click="changeVideosSortord(type)">
                        {{ type }}
                    </li>
                </ul>
            </div>
            <div class="head-right">
                <el-icon size="18px" :class="[{ active: displayStore.isCubeList }, 'icon']"
                    @click="displayStore.changeListDisplayState">
                    <i-ep-Grid /></el-icon>
                <el-icon size="18px" :class="[{ active: !displayStore.isCubeList }, 'icon']"
                    @click="displayStore.changeListDisplayState">
                    <i-ep-Memo /></el-icon>
            </div>
        </div>
        <div v-show="displayStore.isCubeList" class="body">
            <SmallVideoBox :videos-msg="showVideos"></SmallVideoBox>
        </div>
        <div v-show="!displayStore.isCubeList" class="body">
            <ListVideoBox :videos-msg="showVideos">
                <template #videoInfo="{ video }">
                    <div class="videoInfo">
                        <a :href="`/video/${video.videoId}`" target="_blank">
                            <h3 class="title" :title="video.title">{{ video.title }}</h3>
                        </a>
                        <div class="desc" v-html="formatWrapText(video.introduction)" :title="video.introduction">
                        </div>
                        <div class="meta">
                            <div class="viewCounts">
                                <div class="icon"><el-icon><i-ep-VideoPlay /></el-icon></div>
                                <span>{{ formatViewCounts(video.viewCount) }}</span>
                            </div>
                            <span class="creativeTime">{{ formatUploadTime(video.uploadTime) }}</span>
                        </div>
                    </div>
                </template>
            </ListVideoBox>
        </div>
        <div class="papination">
            <el-pagination background layout="prev, pager, next, jumper" :total="total" :page-size="size"
                :page-count="pages" :pager-count="5" @current-change="handelCurrentChange" />
        </div>
    </div>
</template>
<style scoped>
@import url(../../assets/css/views/videos.css)
</style>