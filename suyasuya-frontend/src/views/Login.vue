<script setup lang="ts">
defineOptions({
    name: 'Login'
})

import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import GetCaptchaBtn from '@/components/GetCaptchaBtn.vue'
import { useDisplayStore } from '@/stores/display'
import { useUserStore } from '@/stores/user'
import { loginByCaptcha, loginByPassword } from '@/api/login'
import { getUserInfo } from '@/api/userInfo'
import router from '@/router'

const displayStore = useDisplayStore()
const userStore = useUserStore()
const password = ref<string>('')
const email = ref<string>('')
const captcha = ref<string>('')

const emailReg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/    // 邮箱正则
const isUseCaptchaLogin = ref<boolean>(false)     //是否使用验证码登录
const changeLoginMode = () => {
    isUseCaptchaLogin.value = !isUseCaptchaLogin.value
    displayStore.changeLoginMode()
}
// 登录方法
const login = async () => {
    if (isUseCaptchaLogin.value) {
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
        if (!emailReg.test(email.value)) {
            ElMessage({
                message: '邮箱格式错误',
                type: 'error'
            })
            return
        }
        // 此处调用api进行验证码登录
        const res = await loginByCaptcha(email.value, captcha.value)
        console.log(res)
        if (res.success) {
            localStorage.setItem('token', res.data)
            const res2 = await getUserInfo()
            if (res2.success) {
                console.log(res2)
                userStore.setUserInfo(res2.data)
                // 登录成功后路由跳转
                router.push('/home')
                ElMessage({
                    message: res.message,
                    type: 'success',
                })
            }
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error',
            })
        }
    }
    else {
        if (!email.value) {
            ElMessage({
                message: '请输入电子邮箱',
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
        if (!emailReg.test(email.value)) {
            ElMessage({
                message: '邮箱格式错误',
                type: 'error'
            })
            return
        }

        // 此处调用api进行密码登录
        const res = await loginByPassword(email.value, password.value)
        console.log(res)
        if (res.success) {
            localStorage.setItem('token', res.data)
            const res2 = await getUserInfo()
            if (res2.success) {
                console.log(res2)
                userStore.setUserInfo(res2.data)
                // 登录成功后路由跳转
                router.push('/home')
                ElMessage({
                    message: res.message,
                    type: 'success',
                })
            }
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error',
            })
        }
    }
}




onMounted(() => {
    isUseCaptchaLogin.value = displayStore.isUseCaptchaLogin
})

</script>
<template>
    <div class="bg">
        <!-- <div class="header">
            头部区域待修改
        </div> -->
        <div class="box" @keyup.enter="login">
            <div class="left"></div>
            <div class="right">
                <h4>登 录</h4>
                <div class="loginMod">
                    <input v-if="!isUseCaptchaLogin" v-model="email" class="acc" type="text" placeholder="请输入电子邮箱">
                    <input v-else v-model="email" class="acc" type="text" placeholder="请输入电子邮箱">
                    <input v-if="!isUseCaptchaLogin" v-model="password" type="password" class="acc" placeholder="请输入密码">
                    <div v-else class="ver-code">
                        <input v-model="captcha" type="text" placeholder="请输入验证码">
                        <GetCaptchaBtn :email="email" :type="'login'"></GetCaptchaBtn>
                    </div>

                    <button @click="login" class="submit">提交</button>
                </div>
                <div class="fn">
                    <div class="login-method" @click="changeLoginMode">{{ isUseCaptchaLogin ? '密码登录' : '验证码登录' }}</div>
                    <RouterLink to="/register">注册账号</RouterLink>
                    <RouterLink to="/">找回密码</RouterLink>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
@import '../assets/css/views/login.css';
</style>