<script setup lang="ts">
import { createCollection, deleteCollection, getCollectionVideos, getUserCollections, removeVideoFromCollection, updateCollection } from '@/api/collection'
import SmallVideoBox from '@/components/SmallVideoBox.vue'
import { ElMessage } from 'element-plus'
import { formatUploadTime } from '@/main'
import { useUserStore } from '@/stores/user'
import { onBeforeUnmount, onMounted, ref } from 'vue'

const userStore = useUserStore()

const isSidenavOpen = ref<boolean>(true)


const selectedCollectionId = ref<number | null>(null)        // 记录当前被选中的收藏夹
const createDialogVisible = ref<boolean>(false)    // 创建收藏夹弹窗显示状态
const updateDialogVisible = ref<boolean>(false)    // 修改收藏夹信息弹窗的显示状态
const deleteDialogVisible = ref<boolean>(false)    // 修改删除收藏夹弹窗的显示状态

const openOrCloseSidenavOpen = (): boolean => isSidenavOpen.value = !isSidenavOpen.value
const openCreateDialog = (): boolean => createDialogVisible.value = true

const videos = ref([])        // 要显示的视频信息

// 分页组件相关信息
const currentPage = ref<number>(1)  // 当前页数
const pages = ref<number>(1)        // 总页数
const size = ref<number>(18)        // 每页的视频个数
const total = ref<number>(1)        // 总视频个数

// 新建收藏夹的信息
const newCollectionName = ref<string>('')
const newCollectionDes = ref<string>('')

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
const changeSelectCollection = (collectionId: number) => {
    if (selectedCollectionId.value === collectionId)
        return
    selectedCollectionId.value = collectionId
    getVideosInCollection(collectionId)
}

// 获取收藏夹内视频方法
const getVideosInCollection = async (collectionId: number) => {
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

const handelCurrentChange = (page: number): void => {
    currentPage.value = page
    if (selectedCollectionId.value !== null) {
        getVideosInCollection(selectedCollectionId.value)
    }
}

// 删除收藏夹内视频方法
const removeVideo = async (videoId: number): Promise<void> => {
    if (selectedCollectionId.value === null) return
    try {
        const res = await removeVideoFromCollection(selectedCollectionId.value, videoId)
        if (res.success) {
            await getAllCollections()
            await getVideosInCollection(selectedCollectionId.value)
            ElMessage({
                message: res.message,
                type: 'success'
            })
        }
        else {
            ElMessage({
                message: res.message || '删除视频失败',
                type: 'error'
            })
        }
    } catch (error) {
        console.error('删除视频时发生错误:', error)
        ElMessage({
            message: '删除视频时发生错误',
            type: 'error'
        })
    }
}

const openUpdateDialog = (collectionId: number, collectionName: string, collectionDes: string): void => {
    updateCollectionName.value = collectionName
    updateCollectionDes.value = collectionDes || ''
    updateCollectionId.value = collectionId
    updateDialogVisible.value = true
}

const updateCollectionId = ref<number | null>(null)               // 要修改的收藏夹的id  
const updateCollectionName = ref<string>('')                      // 要修改的收藏夹的名称
const updateCollectionDes = ref<string>('')                       // 要修改的收藏夹的描述


// 更新收藏夹信息方法
const updateCollectionInfo = async (): Promise<void> => {
    if (updateCollectionId.value === null) return
    try {
        const requestData = {
            collectionId: updateCollectionId.value,
            collectionName: updateCollectionName.value,
            description: updateCollectionDes.value
        }
        const res = await updateCollection(requestData)
        console.log(res)
        if (res.success) {
            await getAllCollections()
            ElMessage({
                message: res.message,
                type: 'success'
            })
        }
        else {
            ElMessage({
                message: res.message || '更新收藏夹信息失败',
                type: 'error'
            })
        }
    } catch (error) {
        console.error('更新收藏夹信息时发生错误:', error)
        ElMessage({
            message: '更新收藏夹信息时发生错误',
            type: 'error'
        })
    } finally {
        updateDialogVisible.value = false
    }
}

const deleteCollectionId = ref<number | null>(null) // 要删除的收藏夹id

const openDeleteDialog = (collectionId: number) => {
    deleteCollectionId.value = collectionId
    deleteDialogVisible.value = true
}

// 删除收藏夹内视频方法
const removeCollection = async (): Promise<void> => {
    if (deleteCollectionId.value === null) return
    try {
        const res = await deleteCollection(deleteCollectionId.value)
        console.log(res)
        if (res.success) {
            ElMessage({
                message: res.message,
                type: 'success'
            })
            getAllCollections()
        }
        else {
            ElMessage({
                message: res.message || '删除收藏夹失败',
                type: 'error'
            })
        }
    } catch (error) {
        console.error('删除收藏夹时发生错误:', error)
        ElMessage({
            message: '删除收藏夹时发生错误',
            type: 'error'
        })
    } finally {
        deleteDialogVisible.value = false
    }
}

const hoverCollectionId = ref<number | null>(null)              // 鼠标悬停的收藏夹id

const selectCollectionId = ref<number | null>(null)              // 当前选中的收藏夹id
const editCollection = (collectionId: number) => {
    selectCollectionId.value = selectCollectionId.value === collectionId ? null : collectionId
}

const selectedVideoId = ref<number | null>(null)           // 当前选中的收藏视频id
const editCollectionVideo = (videoId: number): void => {
    selectedVideoId.value = selectedVideoId.value === videoId ? null : videoId
}
const handleGlobalClick = (e: MouseEvent) => {
    const target = e.target as HTMLElement

    // 检查点击是否发生在编辑按钮内部
    const isEditButton =
        target.classList.contains('collection-video-edit-btn') ||
        target.classList.contains('collection-edit-btn') ||
        target.closest('.collection-video-edit-btn') ||
        target.closest('.collection-edit-btn')

    if (!isEditButton) {
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
                            @mouseenter="hoverCollectionId = item.collectionId" @mouseleave="hoverCollectionId = null"
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
                                <div @click="selectedVideoId !== null && removeVideo(selectedVideoId)"
                                    class="edit-panel-item">取消收藏</div>
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
@import url(../../assets/css/views/favorites.css)
</style>