<script setup>
defineOptions({
    name: 'Space'
})
import Header from '@/components/Header.vue'
import { RouterLink, RouterView, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { onMounted, ref, watch } from 'vue';
import { getOtherUserInfo, getUserInfo } from '@/api/userInfo';

const route = useRoute()
const userStore = useUserStore()
const isMe = ref(false)
const currentUserId = ref(+route.params.userId)

// 当前传入的用户信息
const userInfo = ref({})
const signature = ref('')

// 更新个性签名方法(只有用户个人空间页才会触发，无需考虑其他用户)
const updateSign = () => {
    if (signature.value) signature.value = signature.value.trim()
    if (signature.value !== userInfo.value.signature) {
        // 此处调用api存储用户个性标签
        userStore.setSignature(signature.value)
        userInfo.value.signature = signature.value
    }
}

// 数据初始化方法
const initialize = async () => {
    if (currentUserId.value === userStore.getUserId()) {
        isMe.value = true
        // 当前为个人空间界面，数据从用户状态管理仓库获得，此处调用api更新用户个人仓库信息
        userStore.updateUserInfo(route.params.userId)
        userInfo.value = userStore.userInfo
        console.log(userInfo.value)
    }
    else {
        isMe.value = false
        // 当前为其他用户的空间界面,应调用api获取其他用户的个人信息
        const res = await getOtherUserInfo(route.params.userId)
        console.log(res)
        userInfo.value = res.data
    }
    signature.value = userInfo.value.signature
}

watch(() => route.params, (newParams, oldParams) => {
    if (newParams.userId !== oldParams.userId) {
        window.location.reload()                // 当路由参数变化时进行页面刷新
    }
})

onMounted(() => {
    initialize()
})

</script>
<template>
    <div class="bg">
        <Header></Header>
        <div class="body w">
            <div class="h">
                <div class="user">
                    <div class="userInfo">
                        <div class="avatar">
                            <img :src="`http://localhost:8080/avatar/${userInfo.avatar}`" alt="">
                            <a v-if="isMe" href="/account/myAvatar/upload" target="_blank" class="avatar-cover">更换头像</a>
                        </div>
                        <div class="basic">
                            <div class="nickname">{{ userInfo.userName }}</div>
                            <div class="spacing">
                                <input v-if="isMe" v-model="signature" @blur="updateSign"
                                    @keyup.enter="$event.target.blur()" :title="signature" class="my-sign"
                                    placeholder="编辑个性签名">
                                <div v-else class="other-sign">{{ signature }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="wrapper">
                <ul class="nav-bar">
                    <li>
                        <RouterLink :to="{ name: 'spaceHome' }">首页</RouterLink>
                    </li>
                    <li>
                        <RouterLink :to="{ name: 'videos' }">投稿</RouterLink>
                    </li>
                    <li v-if="isMe">
                        <RouterLink :to="{ name: 'favorites' }">收藏夹</RouterLink>
                    </li>
                </ul>
                <div class="statistics">
                    <RouterLink to="">
                        <div class="statistics-item hover-item">
                            <div class="title">关注数</div>
                            <div class="data">{{ userInfo.followCount }}</div>
                        </div>
                    </RouterLink>
                    <RouterLink to="">
                        <div class="statistics-item hover-item">
                            <div class="title">粉丝数</div>
                            <div class="data">{{ userInfo.fanCount }}</div>
                        </div>
                    </RouterLink>
                    <div class="statistics-item">
                        <div class="title">获赞数</div>
                        <div class="data">{{ userInfo.likeCount }}</div>
                    </div>
                    <div class="statistics-item">
                        <div class="title">播放量</div>
                        <div class="data">{{ userInfo.viewCount }}</div>
                    </div>
                </div>
            </div>
            <!-- 注：此处添加二级路由 -->
            <div class="container">
                <RouterView :isMe="isMe" />
            </div>
        </div>
    </div>
</template>
<style scoped>
@import url(../../assets/css/views/space.css);
</style>