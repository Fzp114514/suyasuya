<script setup>
import { formatVideoDuration } from '@/main';

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
        <!-- 具名作用域插槽 -->
        <slot name="videoInfo" :video></slot>
    </div>
</template>
<style scoped>
@import url(../assets/css/components/listVideoBox.css);
</style>