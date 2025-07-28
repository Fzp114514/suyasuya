<script setup lang="ts">
import SecurityRightTitle from '@/components/SecurityRightTitle.vue'
import { useUserStore } from '@/stores/user'


const userStore = useUserStore()

</script>
<template>
    <SecurityRightTitle>我的头像</SecurityRightTitle>
    <div class="avatar-setting">
        <div class="avatar-tool-container">
            <a href="/account/myAvatar/upload">
                <div class="tool-change-avatar">
                    <span>更换头像</span>
                </div>
            </a>
            <img :src="`http://localhost:8080/avatar/${userStore.getUserAvatar()}`" class="avatar">
        </div>
    </div>
</template>
<style scoped>
.avatar-setting {
    padding: 20px 0;
}

.avatar-setting .avatar-tool-container {
    position: relative;
    width: 200px;
    height: 200px;
    margin: 0 auto;
    border: 1px solid #e5e9ef;
    border-radius: 50%;
}

.avatar-tool-container .tool-change-avatar {
    position: absolute;
    top: 50%;
    left: -27px;
    transform: translateY(-50%);
    width: 54px;
    height: 54px;
    background: #00a1d6;
    border: 1px solid #00a1d6;
    border-radius: 50%;
}

.tool-change-avatar span {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 2em;
    color: #fff;
    font-size: 14px;
    line-height: 18px;
}


.avatar-tool-container .avatar {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 96px;
    height: 96px;
    border-radius: 50%;
}
</style>