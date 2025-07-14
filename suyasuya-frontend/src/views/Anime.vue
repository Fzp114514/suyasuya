<script setup>
defineOptions({
    name: 'Anime'
})
import { getCategoryVideos } from '@/api/videoQuery'
import Header from '@/components/Header.vue'
import LargeVideoBox from '@/components/LargeVideoBox.vue'
import PaletteBtn from '@/components/PaletteBtn.vue'
import { useDisplayStore } from '@/stores/display'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
const displayStore = useDisplayStore()

// 测试数据（后端开发时接口设计以此为参考）
// 首页推荐视频

const recommendedVideos = ref()

// 获取首页推荐视频
const getVideos = async () => {
    const res = await getCategoryVideos(5, 12)
    if (res.success) {
        recommendedVideos.value = res.data
        console.log(recommendedVideos.value)
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
}

onMounted(() => {
    getVideos()
})

</script>
<template>
    <div class="bg">
        <div v-show="displayStore.isShow">
            <Header></Header>
            <div class="body">
                <div class="recommend w">
                    <h1 class="title">动漫</h1>
                    <div class="videos">
                        <LargeVideoBox :videos-msg="recommendedVideos"></LargeVideoBox>
                        <!-- <LargeVideoBox :videos-msg="recommendedVideos"></LargeVideoBox> -->
                    </div>
                </div>
            </div>
        </div>
    </div>
    <PaletteBtn>
        <template #otherBtn>
            <div v-if="displayStore.isShow" class="btn" @click="displayStore.changeHomeDisplayState">
                <el-icon><i-ep-View /></el-icon>
            </div>
            <div v-else="displayStore.isShow" class="btn" @click="displayStore.changeHomeDisplayState">
                <el-icon><i-ep-Hide /></el-icon>
            </div>
        </template>
    </PaletteBtn>
</template>
<style scoped>
.bg::before {
    position: fixed;
    z-index: -1;
    width: 100%;
    height: 100%;
    content: '';
    background: url(../assets/imgs/home/bg-anime.jpg) no-repeat fixed center;
    background-size: cover;
    opacity: 0;
    /* 初始背景透明度为0 */
    animation: fadeInBackground 1.5s ease-in-out forwards;
    /* 添加渐变动画 */
}

/* 定义渐变动画 */
@keyframes fadeInBackground {
    0% {
        opacity: 0;
        /* 开始时背景完全透明 */
    }

    100% {
        opacity: 1;
        /* 动画结束时背景完全显示 */
    }
}

.recommend .title {
    display: flex;
    align-items: center;
    height: 85px;
    margin-left: 10px;
    font-weight: normal;
}

.recommend .videos {
    display: flex;
    flex-wrap: wrap;
    padding: 10px;
    margin-bottom: 15px;
    border-radius: 16px;
    background: rgba(255, 255, 255, .8);
}

/* 插槽样式 */

.btn {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 40px;
    height: 40px;
    margin-top: 5px;
    border: 1px solid #e3e5e7;
    font-size: 14px;
    background: rgb(255, 255, 255);
    border-radius: 8px;
    color: #333;
    cursor: pointer;
}

.btn:hover {
    color: black;
    background: rgb(227, 229, 231);
    transition: background-color 0.3s ease;
}
</style>