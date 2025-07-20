<script setup>
defineOptions({
    name: 'Header'
})
import SearchBox from '@/components/SearchBox.vue'
import { getBaseUrl } from '@/main';
import { useUserStore } from '@/stores/user';
import { onMounted } from 'vue';
import { RouterLink } from 'vue-router';

const userStore = useUserStore()

</script>
<template>
    <div class="header">
        <div class="header_nav">
            <ul class="left">
                <li class="home">
                    <RouterLink to="/"><el-icon><i-ep-House /></el-icon>首页</RouterLink>
                </li>
                <li><a href="/study" target="_self"><el-icon><i-ep-Reading /></el-icon>学习</a></li>
                <li><a href="/game" target="_self"><el-icon><i-ep-SwitchFilled /></el-icon>游戏</a></li>
                <li>
                    <a href="/life" target="_self"><el-icon><i-ep-MagicStick /></el-icon>生活</a>
                </li>
                <li><a href="/tittleTattle" target="_self"><el-icon><i-ep-EditPen /></el-icon>杂谈</a></li>
                <li><a href="/anime" target="_self"><el-icon><i-ep-EditPen /></el-icon>动漫</a></li>
            </ul>
            <SearchBox></SearchBox>
            <ul class="right">
                <li class="avatar">
                    <a :href="`/space/${userStore.getUserId()}`" target="_blank">
                        <img :src="`${getBaseUrl()}/avatar/${userStore.getUserAvatar()}`" alt="">
                    </a>
                </li>
                <li><a :href="`/space/${userStore.getUserId()}/favorites`"
                        target="_blank"><el-icon><i-ep-Star /></el-icon>收藏</a></li>
                <li><a href=""><el-icon><i-ep-Message /></el-icon>消息</a></li>
                <li><a :href="`/history`" target="_blank"><el-icon><i-ep-Clock /></el-icon>历史</a></li>
                <li><a href="/account/submitVideo" target="_blank"><el-icon><i-ep-Upload /></el-icon>投稿</a></li>
            </ul>
        </div>
    </div>
</template>
<style scoped>
ul li {
    display: flex;
    justify-content: center;
    height: 100%;
}

/* 设置通用样式 */
a {
    display: flex;
    align-items: center;
    padding: 0 15px;
}

a:hover {
    color: pink;
    border-bottom: 4px solid pink;
}

a i {
    margin-top: 1px;
    margin-right: 3px;
}

.header {
    display: flex;
}

.header_nav {
    flex: 1;
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    width: 100%;
    height: 65px;
    font-size: 16px;
    background: rgba(255, 255, 255, .9);
    box-shadow: 0 2px 4px #00000014;
    z-index: 1;
}

.header_nav .left,
.header_nav .right {
    display: flex;
    align-items: center;
    justify-content: space-between;
    max-width: 500px;
    height: 100%;
}

.header_nav .right {
    justify-content: space-around;
}

/* 设置用户头像样式 */
.right .avatar {
    display: flex;
    align-content: center;
    height: 40px;
    width: 70px;
}

.right .avatar a:hover {
    border-bottom: none;
}

.right .avatar img {
    height: 100%;
    border-radius: 20px;
}
</style>