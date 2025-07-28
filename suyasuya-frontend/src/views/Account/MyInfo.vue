<script setup lang="ts">

import SecurityRightTitle from '@/components/SecurityRightTitle.vue'
import { getUserInfo, updateUserInfo } from '@/api/userInfo'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { onMounted, ref } from 'vue'

interface UserInfo {
    userName: string,
    signature: string,
    gender: string,
    birthday: string
}

const userInfo = ref<UserInfo>({
    userName: '',
    signature: '',
    gender: '',
    birthday: '',
})

const userStore = useUserStore()

const saveUserInfo = async () => {
    try {
        const res = await updateUserInfo(userInfo.value)
        console.log(res)
        if (res.success) {
            await userStore.updateUserInfo()
            ElMessage({
                message: res.message,
                type: 'success'
            })
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error'
            })
        }
    } catch (error) {
        console.error('保存用户信息失败:', error)
        ElMessage({
            message: '保存用户信息失败，请重试',
            type: 'error'
        })
    }

}

onMounted(async () => {
    const res = await getUserInfo()
    if (res.success) userInfo.value = res.data
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }
})

</script>
<template>
    <SecurityRightTitle>我的信息</SecurityRightTitle>
    <div class="userInfo-setting">
        <div class="userInfo-item">
            <div class="label">用户名:</div>
            <input type="text" v-model="userInfo.userName" class="inp username">
        </div>
        <div class="userInfo-item">
            <div class="label">个性签名:</div>
            <textarea v-model="userInfo.signature" class="inp signature"></textarea>
        </div>
        <div class="userInfo-item">
            <div class="label">性别:</div>
            <el-radio-group v-model="userInfo.gender" fill="#00a1d6">
                <el-radio-button label="男" value="Male" />
                <el-radio-button label="女" value="Female" />
                <el-radio-button label="保密" value="Other" />
            </el-radio-group>
        </div>
        <div class="userInfo-item">
            <div class="label">出生日期:</div>
            <el-date-picker v-model="userInfo.birthday" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </div>
        <div class="padding-dom"></div>
        <div @click="saveUserInfo" class="userInfo-save-btn">保存</div>
    </div>
</template>
<style scoped>
@import url(../../assets/css/views/myInfo.css);
</style>