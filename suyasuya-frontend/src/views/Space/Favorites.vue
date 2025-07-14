<script setup>
import { createCollection, deleteCollection, getCollectionVideos, getUserCollections, removeVideoFromCollection, updateCollection } from '@/api/collection';
import SmallVideoBox from '@/components/SmallVideoBox.vue';
import { formatUploadTime } from '@/main';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import { onBeforeUnmount, onMounted, ref } from 'vue';

const userStore = useUserStore()

const isSidenavOpen = ref(true)


const selectedCollectionId = ref()        // 记录当前被选中的收藏夹
const createDialogVisible = ref(false)    // 创建收藏夹弹窗显示状态
const updateDialogVisible = ref(false)    // 修改收藏夹信息弹窗的显示状态
const deleteDialogVisible = ref(false)    // 修改删除收藏夹弹窗的显示状态

const openOrCloseSidenavOpen = () => isSidenavOpen.value = !isSidenavOpen.value
const openCreateDialog = () => createDialogVisible.value = true

const videos = ref([])        // 要显示的视频信息

// 分页组件相关信息
const currentPage = ref(1)  // 当前页数
const pages = ref(1)        // 总页数
const size = ref(18)        // 每页的视频个数
const total = ref(1)        // 总视频个数

// 新建收藏夹的信息
const newCollectionName = ref('')
const newCollectionDes = ref('')

// 创建收藏夹方法
const createNewCollection = async () => {
    const res = await createCollection(newCollectionName.value, newCollectionDes.value)
    console.log(newCollectionName.value, newCollectionDes.value)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        newCollectionName.value = ''
        newCollectionDes.value = ''
        getAllCollections()
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        newCollectionName.value = ''
        newCollectionDes.value = ''
        console.log(res)
    }
    createDialogVisible.value = false
}


const collections = ref()   // 用户的所有收藏夹

// 获取所有用户收藏夹
const getAllCollections = async () => {
    const res = await getUserCollections()
    if (res.success) {
        collections.value = res.data
        if (!selectedCollectionId.value)
            changeSelectCollection(res.data[0].collectionId)
        console.log(collections.value)
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

// 改变显示的收藏夹方法
const changeSelectCollection = collectionId => {
    if (selectedCollectionId.value === collectionId)
        return
    selectedCollectionId.value = collectionId
    getVideosInCollection(collectionId)
}

// 获取收藏夹内视频方法
const getVideosInCollection = async collectionId => {
    selectedCollectionId.value = collectionId
    console.log('触发获取视频方法')
    const res = await getCollectionVideos(collectionId, currentPage.value, size.value)
    if (res.success) {
        videos.value = res.data.list
        pages.value = res.data.pages
        total.value = res.data.total
        console.log(res)
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }
}

const handelCurrentChange = page => {
    currentPage.value = page
    getVideosInCollection(selectedCollectionId.value)
}

// 删除收藏夹内视频方法
const removeVideo = async videoId => {
    const res = await removeVideoFromCollection(selectedCollectionId.value, videoId)
    if (res.success) {
        selectCollectionId.value = ''
        getAllCollections()
        getVideosInCollection(selectedCollectionId.value)
        ElMessage({
            message: res.message,
            type: 'success'
        })
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

const openUpdateDialog = (collectionId, collectionName, collectionDes) => {
    updateCollectionName.value = collectionName
    if (collectionDes)
        updateCollectionDes.value = collectionDes
    updateCollectionId.value = collectionId
    updateDialogVisible.value = true
}

const updateCollectionId = ref('')       // 要修改的收藏夹的id  
const updateCollectionName = ref('')     // 要修改的收藏夹的名称
const updateCollectionDes = ref('')      // 要修改的收藏夹的描述


// 更新收藏夹信息方法
const updateCollectionInfo = async () => {
    const requestData = {
        collectionId: updateCollectionId.value,
        collectionName: updateCollectionName.value,
        description: updateCollectionDes.value
    }
    const res = await updateCollection(requestData)
    console.log(res)
    if (res.success) {
        getAllCollections()
        ElMessage({
            message: res.message,
            type: 'success'
        })
        updateDialogVisible.value = false
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

const deleteCollectionId = ref()

const openDeleteDialog = collectionId => {
    deleteCollectionId.value = collectionId
    deleteDialogVisible.value = true
}

// 删除收藏夹内视频方法
const removeCollection = async () => {
    const res = await deleteCollection(deleteCollectionId.value)
    console.log(res)
    if (res.success) {
        deleteDialogVisible.value = false
        ElMessage({
            message: res.message,
            type: 'success'
        })
        getAllCollections()
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

const hoverCollectionId = ref()

const selectCollectionId = ref(null)              // 当前选中的收藏夹id
const editCollection = collectionId => {
    selectCollectionId.value = selectCollectionId.value === collectionId ? null : collectionId
}

const selectedVideoId = ref(null)           // 当前选中的收藏视频id
const editCollectionVideo = videoId => {
    selectedVideoId.value = selectedVideoId.value === videoId ? null : videoId
}
const handleGlobalClick = e => {
    // 获取事件路径中的所有元素
    const path = e.composedPath()

    // 检查点击是否发生在菜单或按钮内部
    const isInside = path.some(el => {
        return el.classList?.contains('collection-video-edit-btn') ||
            el.classList?.contains('collection-edit-btn')
    })

    if (!isInside) {
        selectedVideoId.value = null
        selectCollectionId.value = null
    }
}

onMounted(() => {
    getAllCollections()
    document.addEventListener('click', handleGlobalClick)
})

onBeforeUnmount(() => {
    document.removeEventListener('click', handleGlobalClick)
})

</script>
<template>
    <div class="space-favourites-bg">
        <div class="fav-sidenav">
            <div class="nav-container">
                <div @click="openOrCloseSidenavOpen" class="favList-title">
                    <p>我的创建</p>
                    <el-icon :class="{ 'rotate-icon': isSidenavOpen }"><i-ep-ArrowDownBold /></el-icon>
                </div>
                <div v-show="isSidenavOpen" class="favList-container">
                    <div @click="openCreateDialog" class="item">
                        <el-icon size="20px">
                            <i-ep-FolderAdd />
                        </el-icon>
                        <span class="text">新建收藏夹</span>
                    </div>
                    <ul>
                        <li v-for="(item, index) in collections" :key="index"
                            @click="changeSelectCollection(item.collectionId)"
                            @mouseenter="hoverCollectionId = item.collectionId" @mouseleave="hoverCollectionId = ''"
                            :class="{ item, active: selectedCollectionId === item.collectionId }">
                            <el-icon size="20px"><i-ep-Folder /></el-icon>
                            <span class="text">{{ item.collectionName }}</span>
                            <span v-show="hoverCollectionId !== item.collectionId" class="num">
                                {{ item.videoCount }}</span>
                            <span v-show="hoverCollectionId === item.collectionId"
                                @click="editCollection(item.collectionId)" class="collection-edit-btn">
                                <span class="icon"></span>
                            </span>
                            <div v-show="item.collectionId === selectCollectionId" class="edit-panel">
                                <div @click="openUpdateDialog(item.collectionId, item.collectionName, item.description)"
                                    class="edit-panel-item">编辑信息</div>
                                <div v-if="!item.isDefault" @click="openDeleteDialog(item.collectionId)"
                                    class="edit-panel-item">删除</div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="fav-main">
            <div class="videos">
                <SmallVideoBox :videos-msg="videos">
                    <template #videoInfo="{ video }">
                        <div class="video-info">
                            <span>收藏时间：</span>
                            <div class="wrap">
                                <span>{{ formatUploadTime(video.collectTime) }}</span>
                                <div @click="editCollectionVideo(video.videoId)" class="collection-video-edit-btn"
                                    title="更多操作">
                                    <div class="icon"></div>
                                </div>
                            </div>
                            <div v-show="selectedVideoId === video.videoId" class="edit-panel">
                                <div @click="removeVideo(selectedVideoId)" class="edit-panel-item">取消收藏</div>
                            </div>
                        </div>
                    </template>
                </SmallVideoBox>
            </div>
            <div class="papination">
                <el-pagination v-if="videos.length" background layout="prev, pager, next, jumper" :total="total"
                    :page-size="size" :page-count="pages" :pager-count="5" @current-change="handelCurrentChange" />
            </div>
        </div>
        <!-- 创建收藏夹弹窗 -->
        <el-dialog v-model="createDialogVisible" title="收藏夹信息" width="410">
            <div class="content">
                <div class="item">
                    <div class="label">收藏夹名称</div>
                    <input v-model="newCollectionName" maxlength="20" class="collection-name" type="text"
                        placeholder="收藏夹名称">
                    <span class="num-limit">{{ newCollectionName.length }}/20</span>
                </div>
                <div class="item">
                    <div class="label">简介</div>
                    <textarea v-model="newCollectionDes" maxlength="200" class="collection-description" type="text"
                        placeholder="可填写简介"></textarea>
                    <span class="num-limit">{{ newCollectionDes.length }}/200</span>
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="createNewCollection">
                        提交
                    </el-button>
                </div>
            </template>
        </el-dialog>
        <!-- 修改收藏夹信息弹窗 -->
        <el-dialog v-model="updateDialogVisible" title="收藏夹信息" width="410">
            <div class="content">
                <div class="item">
                    <div class="label">收藏夹名称</div>
                    <input v-model="updateCollectionName" maxlength="20" class="collection-name" type="text"
                        placeholder="收藏夹名称">
                    <span class="num-limit">{{ updateCollectionName.length }}/20</span>
                </div>
                <div class="item">
                    <div class="label">简介</div>
                    <textarea v-model="updateCollectionDes" maxlength="200" class="collection-description" type="text"
                        placeholder="可填写简介"></textarea>
                    <span class="num-limit">{{ updateCollectionDes.length }}/200</span>
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="updateCollectionInfo">
                        提交
                    </el-button>
                </div>
            </template>
        </el-dialog>
        <!-- 删除收藏夹弹窗 -->
        <el-dialog v-model="deleteDialogVisible" title="提示" width="500">
            <span>确定要删除该收藏夹吗</span>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="deleteDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="removeCollection">
                        确认
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<style scoped>
@import url(../../assets/css/views/favorites.css);
</style>