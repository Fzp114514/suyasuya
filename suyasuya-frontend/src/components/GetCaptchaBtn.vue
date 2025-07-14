<script setup>
const props = defineProps({
    email: String,
    type: String
})
import { getLoginCaptcha } from '@/api/login';
import { getRegisterCaptcha } from '@/api/register';
import { ref } from 'vue';

const emailReg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/    // 邮箱正则
const getCodeBtnContent = ref('获取验证码')
const countdown = ref(60)                    // 当前计时
const coolingTime = ref(60)                 // 冷却时间
const isCodeBtnDisabled = ref(false)

const getVerCode = async () => {
    if (props.email === '') {
        ElMessage({
            message: '请输入电子邮箱',
            type: 'error'
        })
        return
    }
    if (!emailReg.test(props.email)) {
        ElMessage({
            message: '邮箱格式有误，请重新检查',
            type: 'error'
        })
        return
    }
    let res
    if (props.type === 'register') {
        // 此处调用api获取邮箱注册验证码
        res = await getRegisterCaptcha(props.email)
        console.log(res)

    }
    else if (props.type === 'login') {
        res = await getLoginCaptcha(props.email)
        console.log(res)
    }
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        // 发送成功后，启动计时器
        isCodeBtnDisabled.value = true
        getCodeBtnContent.value = `${countdown.value}s`
        const timer = setInterval(() => {
            countdown.value--
            getCodeBtnContent.value = `${countdown.value}s`
            if (countdown.value === 0) {
                clearInterval(timer)
                getCodeBtnContent.value = '获取验证码'
                isCodeBtnDisabled.value = false
                countdown.value = coolingTime.value
            }
        }, 1000);
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
    <button @click="getVerCode" :disabled="isCodeBtnDisabled" class="get-code-btn">
        {{ getCodeBtnContent }}
    </button>
</template>
<style scoped>
.get-code-btn {
    width: 80px;
    background: #00aeec;
    border: none;
    border-radius: 4px;
    color: #ffffff;
    font-size: 12px;
    cursor: pointer;
}
</style>