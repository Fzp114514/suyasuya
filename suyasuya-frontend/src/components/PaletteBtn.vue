<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';

// 置顶按钮显示位置
const topBtnShowPosition = ref(200)
const isShowTopBtn = ref(false)
const handelScroll = () => {
    const currentPosition = window.scrollY
    if (currentPosition > topBtnShowPosition.value) {
        isShowTopBtn.value = true
    }
    else isShowTopBtn.value = false
}
const scrollToTop = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    })
}

onMounted(() => {
    window.addEventListener('scroll', handelScroll)
})
onBeforeUnmount(() => {
    window.addEventListener('scroll', handelScroll)
})

</script>

<template>
    <div class="palette-btn-wrap">
        <a href="" class="btn"><el-icon><i-ep-RefreshRight /></el-icon></a>
        <!-- 具名作用域插槽 -->
        <slot name="otherBtn"></slot>
        <div v-show="isShowTopBtn" class="btn" @click="scrollToTop">
            <el-icon><i-ep-ArrowUp /></el-icon>
        </div>
    </div>
</template>

<style scoped>
.palette-btn-wrap {
    display: flex;
    flex-direction: column;
    position: fixed;
    right: 40px;
    bottom: 40px;
}

.btn {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 40px;
    height: 40px;
    margin-top: 5px;
    border: 1px solid #e3e5e7;
    font-size: 14px;
    background: rgb(255, 255, 255);
    border-radius: 8px;
    color: #333;
    cursor: pointer;
}

.btn:hover {
    color: black;
    background: rgb(227, 229, 231);
    transition: background-color 0.3s ease;
}
</style>