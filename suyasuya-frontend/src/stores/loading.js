import { defineStore } from "pinia"
import { ref } from "vue"

export const useLoadingStore = defineStore('loading', () => {

    const isLoading = ref(false)    // 是否正在加载
    const requesetCount = ref(0)    // 请求计数器
    const minShowTime = 0           // 最小显示时间，单位毫秒
    let showTime = 0                // 开始显示的时间戳

    const getIsLoading = () => {
        return isLoading.value
    }
    const show = () => {
        requesetCount.value++
        // 如果是第一个请求，记录开始时间并显示loading
        if (requesetCount.value === 1) {
            showTime = Date.now()
            isLoading.value = true
        }
        isLoading.value = true
    }
    const hide = () => {
        if (requesetCount.value <= 0)
            return
        requesetCount.value--
        if (requesetCount.value === 0) {
            const elapsedTime = Date.now() - showTime   // 计算已显示时间
            // 如果已显示时间小于最小显示时间，则延迟隐藏
            const remaining = minShowTime - elapsedTime
            console.log(elapsedTime, remaining)
            if (remaining <= 0) {
                isLoading.value = false
            }
            else {
                setTimeout(() => {
                    isLoading.value = false
                }, remaining)
            }
        }
    }

    return {
        isLoading,
        getIsLoading,
        show,
        hide
    }
})