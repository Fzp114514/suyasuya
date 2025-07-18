<script setup>
defineOptions({
    name: 'SpaceHome'
})
const props = defineProps({
    isMe: Boolean,
})
import SmallVideoBox from '@/components/SmallVideoBox.vue';
import { useUserStore } from '@/stores/user';
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { formatUploadTime, formatViewCounts } from '@/main';
import LargeListVideoBox from '@/components/LargeListVideoBox.vue';
import { getVideosByUserByUploadTime, getVideosByUserByViewCount } from '@/api/videoQuery';
import { ElMessage } from 'element-plus';
import { getOtherUserInfo, getUserInfo } from '@/api/userInfo';
import { getRecentCollectedVideos } from '@/api/collection';
import ListVideoBox from '@/components/ListVideoBox.vue';
import { getMasterpiece, updateMasterpiece } from '@/api/userMasterpiece';
import router from '@/router';

const route = useRoute()
const userStore = useUserStore()

// 用户投稿视频（数量为5，按投稿时间排序）
const subVideos = ref([])
// 收藏夹视频  （数量为5，按收藏时间排序）
const favorites = ref([])

// 用户信息
const userInfo = ref({})
const userGender = computed(() => {
    if (userInfo.value.gender === 'Male')
        return '男'
    if (userInfo.value.gender === 'Female')
        return '女'
    if (userInfo.value.gender === 'Other')
        return '保密'
})

// 个人空间公告
const announcement = ref('')

// 文本框计数器显示状态
const isShowCounter = ref(false)
const currentWordCount = ref(0)
const maxWordCount = ref(120)
const getNumber = e => {
    if (currentWordCount.value < maxWordCount.value)
        currentWordCount.value = e.target.value.length
}
// 提示框显示状态
const dialogVisible = ref(false)
// 个人空间公告修改方法(只有用户个人空间页才会触发，无需考虑其他用户)
const changeAnnouncement = async () => {
    isShowCounter.value = false
    if (announcement.value) announcement.value = announcement.value.trim()
    if (announcement.value === userInfo.value.announcement) {
        dialogVisible.value = true
        return
    }
    else {
        userInfo.value.announcement = announcement.value
        console.log(userStore.getAnnouncement())
        // 此处调用api保存公告
        userStore.setAnnouncement(announcement.value)
    }
}
// 个人资料修改方法
const modifyInfo = () => {
    window.open('/account/myInfo')
}


const selectVisible = ref(false)    // 选择代表作弹窗显示状态

// 代表作信息
const masterpiece = ref({
    videoId: '',
    uploadTime: '',
    viewCount: 0,
    duration: 0,
    title: '',
    cover: '',
    introduction: ''
})

const userAllVideos = ref()     // 用户的所有投稿视频

const selectMasterpiece = () => {
    getSubVideos()
}


// 分页组件相关信息
const currentPage = ref(1)  // 当前页数
const pages = ref(1)        // 总页数
const size = ref(4)         // 每页的视频个数
const total = ref(1)        // 总视频个数

const handelCurrentChange = page => {
    currentPage.value = page
    getSubVideos()
}

// 获取用户投稿视频
const getSubVideos = async () => {
    const res = await getVideosByUserByViewCount(userStore.getUserId(), currentPage.value, size.value)
    if (res.success) {
        userAllVideos.value = res.data.records
        pages.value = res.data.pages
        total.value = res.data.total
        selectVisible.value = true
    }
    console.log(res)
}

const setMasterpiece = async videoId => {
    const res = await updateMasterpiece(videoId)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        getUserMasterpiece(route.params.userId)
        selectVisible.value = false
    }
    console.log(res)
}

// 获取用户代表作方法
const getUserMasterpiece = async userId => {
    const res = await getMasterpiece(userId)
    console.log(res)
    if (res.success) {
        masterpiece.value = res.data
    }
}

const logoutConfig = ref(false)     // 登出提示弹窗显示状态
const logout = () => {
    localStorage.removeItem('token')
    userStore.clearUserInfo()
    router.push({ name: 'login' })
}

// 初始化数据方法
const initialize = async () => {
    let res
    if (props.isMe)
        res = await getUserInfo()
    else
        res = await getOtherUserInfo(route.params.userId)
    if (res.success) {
        userInfo.value = res.data
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }

    announcement.value = userInfo.value.announcement
    announcement.value ? currentWordCount.value = announcement.value.length : ''
    // 此处应调用api获取 masterWork, subVideos, favorites等数据
    getUserMasterpiece(route.params.userId)
    const subVideosRes = await getVideosByUserByUploadTime(route.params.userId, 1, 5)
    if (subVideosRes.success) {
        subVideos.value = subVideosRes.data.records
    }
    else {
        ElMessage({
            message: subVideosRes.message,
            type: 'error'
        })
        console.log(subVideosRes)
    }
    const favoritesRes = await getRecentCollectedVideos(route.params.userId, 5)
    if (favoritesRes.success) {
        favorites.value = favoritesRes.data
        console.log(favorites.value)
    }
    else {
        ElMessage({
            message: favoritesRes.message,
            type: 'error'
        })
        console.log(favoritesRes)
    }
}

onMounted(() => {
    initialize()
})

</script>
<template>
    <div class="space-home-bg">
        <div class="content">
            <div v-if="isMe || masterpiece" class="channel-item">
                <h3 class="channel-title">
                    <div>代表作</div>
                    <div v-if="isMe" @click="selectMasterpiece" class="btn">修改</div>
                </h3>
                <LargeListVideoBox v-if="(masterpiece)" :videos-msg="masterpiece"></LargeListVideoBox>
            </div>
            <div class="channel-item">
                <h3 class="channel-title">
                    <div v-if="isMe">我的视频</div>
                    <div v-else>TA的视频</div>
                    <RouterLink :to="{ name: 'videos' }">
                        <div class="btn">更多</div>
                    </RouterLink>
                </h3>
                <div class="videos">
                    <SmallVideoBox :videos-msg="subVideos"></SmallVideoBox>
                </div>
            </div>
            <div class="channel-item">
                <h3 class="channel-title">
                    <div>收藏视频</div>
                    <RouterLink v-if="isMe" :to="{ name: 'favorites' }">
                        <div class="btn">更多</div>
                    </RouterLink>
                </h3>
                <div class="videos">
                    <SmallVideoBox :videos-msg="favorites">
                        <template #videoInfo="{ video }">
                            <span>收藏时间：</span>
                            <span>{{ formatUploadTime(video.collectTime) }}</span>
                        </template>
                    </SmallVideoBox>
                </div>
            </div>
        </div>
        <div class="sidebar">
            <div class="sidebar-item">
                <h3 class="title">公告</h3>
                <div class="ann">
                    <textarea v-if="isMe" v-model="announcement" placeholder="编辑我的空间公告" :maxlength="maxWordCount"
                        @input="getNumber" @focus="isShowCounter = true" @blur="changeAnnouncement"
                        class="input-content"></textarea>
                    <div v-else class="content">{{ announcement }}</div>
                    <div v-if="isShowCounter" class="input-word-counter">
                        {{ currentWordCount }}/{{ maxWordCount }}
                    </div>
                </div>
            </div>
            <div class="sidebar-item">
                <div class="title">
                    <span>个人资料</span>
                    <div v-if="isMe" @click="modifyInfo" class="btn">修改</div>
                </div>
                <div class="user-info">
                    <div class="user-info-item">
                        <span class="info-command">UID</span>
                        <span class="info-value">{{ userInfo.userId }}</span>
                    </div>
                    <div class="user-info-item">
                        <span class="info-command">性别</span>
                        <span class="info-value">{{ userGender }}</span>
                    </div>
                    <div class="user-info-item">
                        <span class="info-command">生日</span>
                        <span class="info-value">{{ userInfo.birthday }}</span>
                    </div>
                </div>
            </div>
            <div v-if="props.isMe" @click="logoutConfig = true" class="logout-btn">退出登录</div>
        </div>
    </div>
    <el-dialog v-model="dialogVisible" title="错误提示" width="500">
        <span>公告修改失败，木有改动</span>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="dialogVisible = false">
                    确认
                </el-button>
            </div>
        </template>
    </el-dialog>
    <el-dialog v-model="selectVisible" title="请选择您的代表作" width="600">
        <div>
            <ListVideoBox :videos-msg="userAllVideos">
                <template #videoInfo="{ video }">
                    <div class="videoInfo">
                        <h3 class="title" :title="video.title">
                            <a :href="`/video/${video.videoId}`" target="_blank">
                                {{ video.title }}
                            </a>
                        </h3>
                        <div class="upname">
                            <a :href="`/space/${video.autherId}`" class="author" target="_blank">
                                {{ video.autherName }}
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
                    <button @click="setMasterpiece(video.videoId)" class="select-btn">确定</button>
                </template>
            </ListVideoBox>
            <div class="papination">
                <el-pagination background layout="prev, pager, next, jumper" :total="total" :page-size="size"
                    :page-count="pages" :pager-count="5" @current-change="handelCurrentChange" />
            </div>
        </div>
    </el-dialog>
    <el-dialog v-model="logoutConfig" title="提示" width="500">
        <span>确定要登出吗</span>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="logoutConfig = false">取消</el-button>
                <el-button type="primary" @click="logout">
                    确认
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>
<style scoped>
@import url(../../assets/css/views/spacehome.css);
</style>