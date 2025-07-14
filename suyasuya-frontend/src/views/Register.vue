<script setup>
defineOptions({
    name: 'Register'
})

import { codeRegister } from '@/api/register';
import GetCaptchaBtn from '@/components/GetCaptchaBtn.vue';
import router from '@/router';
import { ElMessage } from 'element-plus';
import { ref } from 'vue'
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const email = ref('')
const captcha = ref('')

const register = async () => {
    if (!username.value) {
        ElMessage({
            message: '请输入用户名',
            type: 'error',
        })
        return
    }
    if (!password.value) {
        ElMessage({
            message: '请输入密码',
            type: 'error',
        })
        return
    }
    if (!confirmPassword.value) {
        ElMessage({
            message: '请输入确认密码',
            type: 'error',
        })
        return
    }
    if (password.value !== confirmPassword.value) {
        ElMessage({
            message: '确认密码有误',
            type: 'error',
        })
        return
    }
    if (!email.value) {
        ElMessage({
            message: '请输入电子邮箱',
            type: 'error',
        })
        return
    }
    if (!captcha.value) {
        ElMessage({
            message: '请输入验证码',
            type: 'error',
        })
        return
    }

    // 此处应调用api进行注册操作
    const res = await codeRegister(username.value, password.value, email.value, captcha.value)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        router.push('/login')
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }

}

</script>
<template>
    <html>
    <!-- <div class="header">
    </div> -->
    <div class="box" @keyup.enter="register">
        <div class="left"></div>
        <div class="right">
            <h4>注册账号</h4>
            <div class="loginMod">
                <input v-model="username" class="acc" type="text" placeholder="用户名">
                <input v-model="password" class="acc" type="password" placeholder="用户密码">
                <input v-model="confirmPassword" class="acc" type="password" placeholder="确认用户密码">
                <input v-model="email" class="acc" type="text" placeholder="电子邮箱">
                <div class="ver-code">
                    <input v-model="captcha" type="text" placeholder="验证码">
                    <GetCaptchaBtn :email="email" :type="'register'"></GetCaptchaBtn>
                </div>
                <button @click="register" class="submit">注册</button>
            </div>
            <div class="fn">
                <RouterLink to="/login">返回登录</RouterLink>
            </div>
        </div>
    </div>

    </html>
</template>
<style scoped>
@import '../assets/css/views/register.css';
</style>