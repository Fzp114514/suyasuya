<script setup>
import Header from '@/components/Header.vue';
defineOptions({
    name: 'Account'
})
</script>
<template>
    <div class="bg">
        <Header></Header>
        <div class="security-content">
            <div class="security_left">
                <div class="security_title">个人中心</div>
                <ul class="ser_ul">
                    <li>
                        <RouterLink :to="{ name: 'accountHome' }">首页</RouterLink>
                    </li>
                    <li>
                        <RouterLink :to="{ name: 'myInfo' }">我的信息</RouterLink>
                    </li>
                    <li>
                        <RouterLink :to="{ name: 'myAvatar' }">我的头像</RouterLink>
                    </li>
                    <li>
                        <RouterLink :to="{ name: 'submitVideo' }">投稿视频</RouterLink>
                    </li>
                </ul>
            </div>
            <div class="security_right">
                <RouterView></RouterView>
            </div>
        </div>
    </div>
</template>
<style scoped>
@import url(../../assets/css/views/account.css);
</style>