<script setup lang="ts">
import router from '@/router'
import { usekeywordsStore } from '@/stores/keywords'
import { useSearchStore } from '@/stores/search'
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

const keywordsStore = usekeywordsStore()
const searchStore = useSearchStore()

// 搜索模块相关方法
const isShowSearchPanel = ref(false)
const hoveredItemIndex = ref<number | null>(null)      // 记录当前悬停的历史项索引

// 获取Dom元素 search_box
const searchBox = ref<HTMLElement | null>(null)
// 隐藏搜索面板方法     注意：要阻止remove-icon所触发的事件的传递，否则会引发逻辑冲突
const handleClickOutside = (e: Event) => {
    if (searchBox.value && !(searchBox.value as HTMLElement).contains(e.target as Node)) {
        isShowSearchPanel.value = false
    }
}
watch(isShowSearchPanel, newValue => {
    if (newValue) {
        document.addEventListener('click', handleClickOutside)
    }
    else {
        document.removeEventListener('click', handleClickOutside)
    }
})
// 组件卸载前移除事件监听，避免内存泄漏
onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside)
})
const clearKeywords = () => {
    keywordsStore.clearKeywords()
    isShowSearchPanel.value = false
}
const removeKeywordByIndex = (index: number) => {
    keywordsStore.removeKeywordByIndex(index)
    if (keywordsStore.getKeywordsLength() === 0) isShowSearchPanel.value = false
}

// 搜索方法
const search = (keyword: string) => {
    if (!keyword || !keyword.trim()) {
        return
    }
    keyword = keyword.trim()
    // 删除重复关键词
    if (keywordsStore.findKeyword(keyword))
        keywordsStore.removeKeywordByValue(keyword)
    keywordsStore.addKeyword(keyword)
    isShowSearchPanel.value = false
    const url = router.resolve({ path: `/search/${keyword}/videos` }).href
    window.open(url, '_blank')
    // router.push({
    //     path: `/search/${keyword}/videos`
    // })
}

</script>
<template>
    <div class="search_box" ref="searchBox">
        <div :class="['search', { 'change_search_style': isShowSearchPanel }]">
            <input v-model="searchStore.keyword"
                @focus="keywordsStore.getKeywordsLength() ? isShowSearchPanel = true : isShowSearchPanel = false"
                @keyup.enter="search(searchStore.keyword)" type="text" placeholder="请输入关键词">
            <button @click="search(searchStore.keyword)"><el-icon><i-ep-Search /></el-icon></button>
        </div>
        <div v-show="isShowSearchPanel" class="search_panel">
            <div class="header">
                <div class="title">搜索历史</div>
                <div class="clear" @click="clearKeywords">清空</div>
            </div>
            <div class="search_histories">
                <div v-for="(item, index) in keywordsStore.keywords" :key="index" class="item"
                    @mouseenter="hoveredItemIndex = index" @mouseleave="hoveredItemIndex = null" @click="search(item)">
                    <div class="text">{{ item }}</div>
                    <div v-show="hoveredItemIndex === index" class="remove-icon"
                        @click.stop="removeKeywordByIndex(index)">✕
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
@import url(../assets/css/components/header/searchBox.css);
</style>