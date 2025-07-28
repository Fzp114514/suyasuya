<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useSearchStore } from '@/stores/search'

const searchStore = useSearchStore()

const route = useRoute()

onMounted(() => {
    // 确保 keyword 是字符串类型
    const keyword = Array.isArray(route.params.keyword)
        ? route.params.keyword[0]
        : route.params.keyword;

    searchStore.setKeyword(String(keyword))
    searchStore.searchVideos()
})
</script>
<template>
    <div class="body w">
        <div class="videos">
            <LargeVideoBox :videos-msg="searchStore.searchResults"></LargeVideoBox>
        </div>
        <div class="papination">
            <el-pagination background layout="prev, pager, next, jumper" :total="searchStore.currentPage"
                :page-size="searchStore.size" :page-count="searchStore.pages" :pager-count="5"
                @current-change="searchStore.handelCurrentChange" />
        </div>
    </div>
</template>
<style scoped>
.body {
    margin-bottom: 60px;
}

.videos {
    display: flex;
    flex-wrap: wrap;
}

.papination {
    display: flex;
    justify-content: center;
    margin: 30px 0;
}
</style>