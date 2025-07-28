<script setup lang="ts">
import { formatUploadTime, formatVideoDuration, formatViewCounts, getBaseUrl } from '@/main'


// 要渲染的视频数据，通过父组件传值获得
const props = defineProps({
    videosMsg: Object
})
// 传值对象格式参考
// {
//     videoId: 2,
//     authorId: 2,
//     author: '不贰郭',
//     uploadTime: "2025-03-07T11:40:48.000+00:00",
//     viewCount: '2038021',
//     duration: '1232',
//     title: '68万人评出9.1高分，隐喻铺天盖地，这部动画是写给成年人看的《哈尔的移动城堡》',
//     cover: '61d0e3cf-fbcc-45b6-8c01-6aaa80679224.jpeg',
// },



</script>
<template>
    <div class="videoBox" v-for="video in videosMsg" :key="video.videoId">
        <a :href="`/video/${video.videoId}`" target="_blank">
            <div class="videoCover">
                <img class="pic" :src="`${getBaseUrl()}/cover/${video.cover}`" :title="video.title">
                <div class="mask" :title="video.title">
                    <div class="info">
                        <div class="viewCounts">
                            <div class="icon">
                                <el-icon><i-ep-VideoPlay /></el-icon>
                            </div>
                            <span>{{ formatViewCounts(video.viewCount) }}</span>
                        </div>
                        <div class="length">
                            <span>{{ formatVideoDuration(video.duration) }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </a>
        <h4 class="title">
            <a :href="`/video/${video.videoId}`" :title="video.title" target="_blank">{{ video.title }}</a>
        </h4>
        <!-- 具名作用域插槽 -->
        <slot name="detailInfo" :video>
            <div class="detailInfo">
                <a :href="`/space/${video.authorId}`" class="author" target="_blank">
                    <span :title="video.autherName">{{ video.authorName }}</span>
                </a>
                <span class="creativeTime">{{ formatUploadTime(video.uploadTime) }}</span>
            </div>
        </slot>
    </div>
</template>
<style scoped>
@import url(../assets/css/components/largeVideoBox.css);
</style>