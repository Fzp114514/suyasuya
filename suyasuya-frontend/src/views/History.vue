<script setup lang="ts">
import LargeVideoBox from '@/components/LargeVideoBox.vue'
import { clearHistory, deleteWatchHistory, getWatchHistory } from '@/api/watchHistory'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'

defineOptions({
    name: 'History'
})

// 分页组件相关信息
const currentPage = ref<number>(1)      // 当前页数
const pages = ref<number>(1)            // 总页数
const size = ref<number>(16)            // 每页的视频个数
const total = ref<number>(1)            // 总视频个数


const handelCurrentChange = (page: number) => {
    currentPage.value = page
    getHistoryVideos()
    console.log(currentPage.value)
}

const historyVideos = ref()

// 获取所有历史记录方法
const getHistoryVideos = async () => {
    const res = await getWatchHistory(currentPage.value, size.value)
    console.log(res)
    if (res.success) {
        total.value = res.data.total
        pages.value = res.data.pages
        historyVideos.value = res.data.list
        // console.log(historyVideos.value)
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

// 清空历史记录方法
const deleteHistory = async (videoId: number) => {
    const res = await deleteWatchHistory(videoId)
    console.log(res)
    if (res.success) {
        getHistoryVideos()
    }
}

const clearAllDialog = ref(false)       // 清空历史记录弹窗

const clearAllHistory = async () => {
    const res = await clearHistory()
    console.log(res)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        getHistoryVideos()
        clearAllDialog.value = false
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }
}

const formatWatchTime = (watchTime: number) => {
    const date: Date = new Date(watchTime)
    const now: Date = new Date()

    // 创建对比用的日期（去掉时间部分）
    const createDateWithoutTime = (d: Date) => new Date(d.getFullYear(), d.getMonth(), d.getDate())

    // 获取对比日期
    const currentDate: Date = createDateWithoutTime(now)
    const watchDate: Date = createDateWithoutTime(date)
    const yesterdayDate: Date = new Date(currentDate)
    yesterdayDate.setDate(yesterdayDate.getDate() - 1)

    // 格式时间部分
    const formatTime = (d: Date) => {
        const hours = String(d.getHours()).padStart(2, '0')
        const minutes = String(d.getMinutes()).padStart(2, '0')
        return `${hours}:${minutes}`
    };

    // 判断日期关系
    if (watchDate.getTime() === currentDate.getTime()) {
        return `今天 ${formatTime(date)}`
    } else if (watchDate.getTime() === yesterdayDate.getTime()) {
        return `昨天 ${formatTime(date)}`
    } else {
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${month}-${day} ${formatTime(date)}`
    }
}

onMounted(() => {
    getHistoryVideos()
})

</script>
<template>
    <Header></Header>
    <div class="body w">
        <div class="navbar">
            <div class="title">
                <el-icon><i-ep-Timer /></el-icon>
                <span class="text">观看历史记录</span>
            </div>
            <button @click="clearAllDialog = true" class="clearAll-btn">清空历史</button>
        </div>
        <div class="videos">
            <LargeVideoBox :videos-msg="historyVideos">
                <template #detailInfo="{ video }">
                    <div class="detailInfo">
                        <a :href="`/space/${video.authorId}`" class="author" target="_blank">
                            <span :title="video.autherName">{{ video.authorName }}</span>
                        </a>
                        <div class="wrap">
                            <span class="creativeTime">{{ formatWatchTime(video.watchTime) }}</span>
                            <el-icon @click="deleteHistory(video.videoId)" title="删除记录" size="18px"
                                class="delete-btn"><i-ep-Delete /></el-icon>
                        </div>
                    </div>
                </template>
            </LargeVideoBox>
        </div>
        <div class="papination">
            <el-pagination background layout="prev, pager, next, jumper" :total="currentPage" :page-size="size"
                :page-count="pages" :pager-count="5" @current-change="handelCurrentChange" />
        </div>
    </div>
    <el-dialog v-model="clearAllDialog" title="删除记录" width="500">
        <span>确认要删除所有历史记录吗？（该操作无法撤回）</span>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="clearAllDialog = false">取消</el-button>
                <el-button type="primary" @click="clearAllHistory">
                    确认
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>
<style scoped>
.bg::before {
    position: fixed;
    z-index: -1;
    width: 100%;
    height: 100%;
    content: '';
}

.navbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 20px 0;
    padding-left: 10px;
    height: 40px;
}

.navbar .title {
    display: flex;
    align-items: center;
    font-size: 28px;
}

.navbar .title .text {
    margin-left: 10px;
}

.navbar .clearAll-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 30px;
    width: 100px;
    min-width: fit-content;
    height: 34px;
    color: #18191c;
    background: #fff;
    border: 1px solid #e3e5e7;
    border-radius: 8px;
    white-space: nowrap;
    transition: width .3s;
    transition-delay: .1s;
    cursor: pointer;
}

.navbar .clearAll-btn:hover {
    background: #e3e5e7;
}

.videos {
    display: flex;
    flex-wrap: wrap;
}

.papination {
    display: flex;
    justify-content: center;
    margin: 30px 0;
    width: 100%;
}

/* 插槽相关样式 */

.detailInfo {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 280px;
    height: 15px;
    margin: 5px;
    font-size: 13px;
}

.detailInfo .author {
    display: flex;
    align-items: center;
    color: #9499A0;
}

.detailInfo .author span {
    display: inline-block;
    width: 150px;
    color: #9499A0;
    /* 设置文本溢出处理 */
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.detailInfo .author:hover,
.detailInfo .author span:hover {
    color: #00aeec;
    transition: color 0.3s ease;
}

.detailInfo .creativeTime {
    color: #9499A0;
}

.detailInfo .wrap {
    display: flex;
    align-items: center;
}

.detailInfo .wrap .delete-btn {
    margin-left: 5px;
    color: #9499a0;
    cursor: pointer;
}

.detailInfo .wrap .delete-btn:hover {
    color: #00aeec;
    transition: color 0.3s ease;
}
</style>