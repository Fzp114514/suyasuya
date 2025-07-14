<script setup>
defineOptions({
    name: 'Search'
})
import { useSearchStore } from '@/stores/search';
import { inject, onMounted, ref } from 'vue';

const searchStore = useSearchStore()

// 界面状态控制
const isSearchVideos = ref(true)

const VideosSortord = ref(['最多播放', '最新发布', '最多收藏'])
const UsersSortord = ref(['最多粉丝数', '最多播放量', '最多点赞数'])
const selectType = ref('最多播放')

const changeSelectType = value => {
    if (value === '视频') {
        isSearchVideos.value = true
        selectType.value = '最多播放'
    }
    else if (value === '用户') {
        isSearchVideos.value = false
        selectType.value = '最多粉丝数'
    }
}

const changeSortord = type => {
    if (selectType.value === type)
        return
    selectType.value = type
    switch (selectType.value) {
        case '最多播放':
            searchStore.changeVideosSortord('view_count')
            break
        case '最新发布':
            searchStore.changeVideosSortord('upload_time')
            break
        case '最多收藏':
            searchStore.changeVideosSortord('collection_count')
            break
        default:
            break
    }
}

</script>
<template>
    <div class="bg">
        <Header></Header>
        <div class="body w">
            <div class="navbar">
                <ul class="search-type">
                    <li>
                        <RouterLink :to="{ name: 'searchVideos' }" @click="changeSelectType('视频')">视频</RouterLink>
                    </li>
                    <li>
                        <RouterLink :to="{ name: 'searchUsers' }" @click="changeSelectType('用户')">用户</RouterLink>
                    </li>
                </ul>
                <ul v-show="isSearchVideos" class="search-video-detail">
                    <li v-for="(type, index) in VideosSortord" :key="index" @click="changeSortord(type)"
                        :class="{ active: selectType === type }">
                        {{ type }}
                    </li>
                </ul>
                <ul v-show="!isSearchVideos" class="search-user-detail">
                    <li v-for="(type, index) in UsersSortord" :key="index" @click="changeSortord(type)"
                        :class="{ active: selectType === type }">
                        {{ type }}
                    </li>
                </ul>
            </div>
            <div class="videos">
                <RouterView></RouterView>
            </div>
        </div>
    </div>
</template>
<style scoped>
.bg::before {
    position: fixed;
    z-index: -1;
    width: 100%;
    height: 100%;
    content: '';
}

.router-link-exact-active {
    color: #00aeec;
    border-bottom: 2px solid #00aeec;
}

.navbar {
    display: flex;
    align-items: center;
    margin: 20px 0;
    height: 40px;
    /* background: pink; */
}

.navbar .search-type {
    display: flex;
    width: 200px;
    height: 100%;
    font-size: 18px;
}

.navbar li a {
    display: flex;
    height: 100%;
    line-height: 40px;
}

.search-type li {
    padding: 0 10px;
}

.navbar .search-video-detail,
.navbar .search-user-detail {
    display: flex;
    height: 100%;
    font-size: 12px;
    cursor: pointer;
}

.navbar .search-video-detail li,
.navbar .search-user-detail li {
    height: 100%;
    line-height: 40px;
    margin: 0 10px;
}

.navbar .search-video-detail li.active,
.navbar .search-user-detail li.active {
    color: #00aeec;
    border-bottom: 2px solid #00aeec;

    cursor: pointer;
}
</style>