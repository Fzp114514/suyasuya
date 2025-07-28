import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import persist from 'pinia-plugin-persistedstate'
import VueCropper from 'vue-cropper'


import '@/assets/css/global.css'
import 'element-plus/dist/index.css'
import 'vue-cropper/dist/index.css'
import App from './App.vue'

const app = createApp(App)
app.use(createPinia().use(persist))
app.use(VueCropper)
app.use(router)
app.mount('#app')

export const getBaseUrl = () =>
    import.meta.env.VITE_API_BASE_URL

// 防抖函数
export const debounce = <T extends (...args: any[]) => any>(fn: T, delay: number): ((...args: Parameters<T>) => void) => {
    let timer: ReturnType<typeof setTimeout> | null = null
    return (...args: Parameters<T>) => {
        if (timer)
            clearTimeout(timer)
        timer = setTimeout(() => {
            fn.apply(this, args)
        }, delay)
    }
}

// 节流函数
export const throttle = <T extends (...args: any[]) => any>(fn: T, delay: number): ((...args: Parameters<T>) => void) => {
    let timer: ReturnType<typeof setTimeout> | null = null
    return (...args: Parameters<T>) => {
        if (timer)
            return
        timer = setTimeout(() => {
            fn.apply(this, args)
            timer = null
        }, delay)
    }
}

// 格式化操作
export const formatViewCounts = (viewCounts: number): string | number => {
    if (viewCounts > 100_000_000) {
        const value = (viewCounts / 100_000_000).toFixed(1);
        const numValue = parseFloat(value);

        // 检查是否为整数（小数部分为0）
        return numValue % 1 === 0
            ? `${Math.floor(numValue)}亿`
            : `${value}亿`;
    }

    if (viewCounts > 10_000) {
        const value = (viewCounts / 10_000).toFixed(1);
        const numValue = parseFloat(value);

        // 检查是否为整数（小数部分为0）
        return numValue % 1 === 0
            ? `${Math.floor(numValue)}万`
            : `${value}万`;
    }
    return viewCounts
}

export const formatUploadTime = (uploadTime: number): string => {
    const date = new Date(uploadTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
};

export const formatUploadTimeDetail = (uploadTime: number): string => {
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
export const formatVideoDuration = (time: number): string => {
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

export const formatWrapText = (text: string): string => {
    // 将字符串中的 \r 或 \n 替换为 <br> 标签
    return text.replace(/(\r\n|\r|\n)/g, '<br>')
}
