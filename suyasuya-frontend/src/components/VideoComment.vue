<script setup>
import { formatWrapText } from '@/main';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore()

const props = defineProps({
    commentContents: Object
})

const likeComment = (commentId, islike) => {
    console.log(commentId, islike)
    if (islike) {
        // 此处调用api将点赞评论信息存储进数据库

    }
}
</script>

<template>
    <div v-for="item in commentContents" class="comment">
        <div class="avatar">
            <a :href="`/space/${item.userId}`" target="_blank">
                <img :src="`http://localhost:8080/avatar/${userStore.getUserAvatar()}`" alt="">
            </a>
        </div>
        <div class="main">
            <div class="header">
                <div class="nickName">
                    <a href="">{{ item.nickName }}</a>
                </div>
            </div>
            <div class="content" v-html="formatWrapText(item.commentContent)">
            </div>
            <div class="comment-action">
                <div class="pubdate">{{ item.commentTime }}</div>
                <div @click="likeComment(item.commentId, item.islike)" class="comment-btn">
                    <span v-if="item.islike" class="icon islike"></span>
                    <span v-else class="icon like"></span>
                    <span>{{ item.likes }}</span>
                </div>
                <div class="comment-btn">
                    <span class="icon reply"></span>
                    <span>回复</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
@import url(../assets/css/components/videoComment.css);
</style>