<script setup>
import { useLoadingStore } from '@/stores/loading'
import { computed, ref, watch } from 'vue'

const loadingStore = useLoadingStore()
const isLoading = computed(() => loadingStore.isLoading)

// 控制内部显示状态，用于处理过渡效果
const isVisible = ref(false)

watch(isLoading, (newValue) => {
    if (newValue)
        // 立即显示
        isVisible.value = true
    else
        // 延迟隐藏以完成过渡动画,需与过渡时间匹配
        setTimeout(() => {
            isVisible.value = false
        }, 300)
})

</script>
<template>
    <Transition name="fate">
        <div v-if="isVisible" class="bg">
            <div class="loading"></div>
        </div>
    </Transition>
</template>
<style scoped>
.bg {
    position: fixed;
    width: 100%;
    height: 100%;
    /* 确保loading在最上层 */
    z-index: 9999;
}

.bg .loading {
    position: fixed;
    top: 70px;
    left: 20px;
    /* transform: translate(-50%, -50%); */
    width: 100px;
    height: 100px;
    border-radius: 10px;
    background: url('../assets/imgs/loading.gif') no-repeat center center;
    background-size: cover;
    opacity: 0.7;
}

/* 过渡效果 */
.fade-enter-active,
.fade-leave-active {
    transition: transform 0.3s ease;
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

/* 微妙的脉动动画 */
.bg .loading {
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.05);
    }

    100% {
        transform: scale(1);
    }
}
</style>