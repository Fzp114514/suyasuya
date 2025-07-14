<script setup>
import { useSearchStore } from '@/stores/search';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const searchStore = useSearchStore()

const route = useRoute()

onMounted(() => {
    searchStore.setKeyword(route.params.keyword)
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