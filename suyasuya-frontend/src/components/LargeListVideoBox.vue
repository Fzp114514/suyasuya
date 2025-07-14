<script setup>
import { formatUploadTime, formatVideoDuration, formatViewCounts, formatWrapText, getBaseUrl } from '@/main';

const props = defineProps({
    videosMsg: Object
})

</script>
<template>
    <div class="videoBox">
        <a :href="`/video/${videosMsg.videoId}`" target="_blank">
            <div class="cover" :title="videosMsg.title">
                <img :src="`${getBaseUrl()}/cover/${videosMsg.cover}`" alt="">
                <div class="length">{{ formatVideoDuration(videosMsg.duration) }}</div>
            </div>
        </a>
        <div class="info">
            <h4 class="title" :title="videosMsg.title">
                <a :href="`/video/${videosMsg.videoId}`" target="_blank">{{ videosMsg.title }}</a>
            </h4>
            <div class="meta">
                <div class="viewCounts">
                    <div class="icon">
                        <el-icon><i-ep-VideoPlay /></el-icon>
                    </div>
                    <span>{{ formatViewCounts(videosMsg.viewCount) }}</span>
                </div>
                <div class="length">
                    <span>{{ formatUploadTime(videosMsg.uploadTime) }}</span>
                </div>
            </div>
            <div class="desc" :title="videosMsg.introduction" v-html="formatWrapText(videosMsg.introduction)">
            </div>
        </div>
    </div>
</template>
<style scoped>
@import url(../assets/css/components/largeListVideoBox.css);
</style>