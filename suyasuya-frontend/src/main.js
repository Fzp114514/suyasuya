import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import persist from 'pinia-plugin-persistedstate'
import VueCropper from 'vue-cropper'


import '@/assets/css/global.css'
import 'element-plus/dist/index.css'
import 'vue-cropper/dist/index.css'

const app = createApp(App)
app.use(createPinia().use(persist))
app.use(VueCropper)
app.use(router)
app.mount('#app')

export const getBaseUrl = () =>
    import.meta.env.VITE_API_BASE_URL

// 防抖函数
export const debounce = (fn, delay) => {
    let timer = null
    return function () {
        if (timer)
            clearTimeout(timer)
        timer = setTimeout(() => {
            fn.apply(this, arguments)
        }, delay)
    }
}

// 节流函数
export const throttle = (fn, delay) => {
    let timer = null
    return function () {
        if (timer)
            return
        timer = setTimeout(() => {
            fn.apply(this, arguments)
            timer = null
        }, delay)
    }
}

// 格式化操作
export const formatViewCounts = viewCounts => {
    if (viewCounts > 100000000) {
        const value = (viewCounts / 100000000).toFixed(1)
        if (value & 1 === 0)
            return `${parseInt(value)}亿`
        else return `${value}亿`
    }
    else if (viewCounts > 10000) {
        const value = (viewCounts / 10000).toFixed(1)
        if (value % 1 === 0)
            return `${parseInt(value)}万`
        else return `${value}万`
    }
    else return viewCounts
}

export const formatUploadTime = uploadTime => {
    const date = new Date(uploadTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
};

export const formatUploadTimeDetail = uploadTime => {
    const date = new Date(uploadTime)
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0') // 月份从0开始，所以加1
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化视频时长
export const formatVideoDuration = time => {
    const hours = Math.floor(time / 3600)
    const minutes = Math.floor((time % 3600) / 60)
    const seconds = Math.round(time % 60)
    const formattedHours = String(hours).padStart(2, '0')
    const formattedMinutes = String(minutes).padStart(2, '0')
    const formattedSeconds = String(seconds).padStart(2, '0')
    if (time > 3600)
        return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`
    else
        return `${formattedMinutes}:${formattedSeconds}`
}

export const formatWrapText = text => {
    // 将字符串中的 \r 或 \n 替换为 <br> 标签
    return text.replace(/(\r\n|\r|\n)/g, '<br>')
}
