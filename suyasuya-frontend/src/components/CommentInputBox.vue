<script setup>
import { getBaseUrl } from '@/main';
import { useUserStore } from '@/stores/user';
import { ref } from 'vue';

const userStore = useUserStore()

const isCommenting = ref(false)
const comment = ref('')                         // 要发布的内容
const postComment = () => {
    console.log(comment.value)

    // 注：此处调用api发布评论
    comment.value = ''
    isCommenting.value = false
}

</script>
<template>
    <div class="comment-box">
        <div class="avatar">
            <a :href="`/space/${userStore.getUserId()}`" target="_blank">
                <img :src="`${getBaseUrl()}/avatar/${userStore.getUserAvatar()}`" alt="">
            </a>
        </div>
        <div class="container">
            <div :class="['comment-area', { active: isCommenting }]">
                <textarea v-model="comment" @focus="isCommenting = true" class="content"
                    placeholder="评论一时爽，一直评论一直爽"></textarea>
            </div>
            <div v-show="isCommenting" class="commment-editor">
                <div></div>
                <button @click="postComment" class="sub">发布</button>
            </div>
        </div>
    </div>
</template>
<style scoped>
/* ================评论发表组件样式=============== */

.avatar a:hover {
    border-bottom: none;
}

.avatar img {
    width: 48px;
    height: 48px;
    border-radius: 24px;
}

.comment-box {
    display: flex;
    width: 100%;
}

.comment-box .avatar {
    display: flex;
    justify-content: center;
    align-content: center;
    width: 80px;
    height: 50px;
}

.comment-box .container {
    display: flex;
    flex-direction: column;
    width: 100%;
}

.comment-box .comment-area {
    width: 100%;
    height: 50px;
    border: 1px solid rgb(241, 242, 243);
    border-radius: 6px;
    background: rgb(241, 242, 243);

    overflow-y: auto;
    transition: all 0.3s ease;
}

.comment-box .comment-area:hover {
    background: rgb(255, 255, 255);
    border: 1px solid rgb(201, 204, 208);
}

.comment-box .active {
    background: rgb(255, 255, 255);
    border: 1px solid rgb(201, 204, 208);
}

.comment-box .comment-area .content {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
    height: 100%;
    border: none;
    padding: 5px 8px;
    background: transparent;
    font-size: 14px;
    font-family: "Microsoft YaHei";
    resize: none;
    outline: none;
    line-height: 1.5;

}

.comment-box .comment-area .content::placeholder {
    color: #9499a0;
}

.comment-box .comment-area .content:focus {
    border: 0;
}

.comment-box .commment-editor {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
}

.comment-box .commment-editor .sub {
    width: 70px;
    height: 30px;
    border: none;
    border-radius: 4px;
    background: #00aeec80;
    color: rgb(255, 255, 255);
    font-size: 14px;

    cursor: pointer;
}

.comment-box .commment-editor .sub:hover {
    background: #00aeec;
}
</style>