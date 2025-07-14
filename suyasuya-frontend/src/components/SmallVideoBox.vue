<script setup>
import { formatUploadTime, formatVideoDuration, formatViewCounts } from '@/main';


const props = defineProps({
    videosMsg: Object
})

</script>
<template>
    <div class="videoBox" v-for="video in videosMsg" :key="video.videoId">
        <a :href="`/video/${video.videoId}`" target="_blank">
            <div class="cover" :title="video.title">
                <img :src="`http://localhost:8080/cover/${video.cover}`" alt="">
                <div class="length">{{ formatVideoDuration(video.duration) }}</div>
            </div>
        </a>
        <a :href="`/video/${video.videoId}`" class="title" :title="video.title" target="_blank">{{ video.title }}</a>
        <div class="meta">
            <!-- 具名作用域插槽 -->
            <slot name="videoInfo" :video>
                <div class="viewCounts">
                    <div class="icon"><el-icon><i-ep-VideoPlay /></el-icon></div>
                    <span>{{ formatViewCounts(video.viewCount) }}</span>
                </div>
                <div class="creativeTime">
                    {{ formatUploadTime(video.uploadTime) }}
                </div>
            </slot>
        </div>
    </div>
</template>
<style scoped>
@import url(../assets/css/components/smallVideoBox.css);
</style>