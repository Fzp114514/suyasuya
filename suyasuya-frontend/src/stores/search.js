import { searchVideosByKeyword } from "@/api/search";
import { defineStore } from "pinia";
import { ref } from "vue";
import { useUserStore } from "./user";

// 此仓库用于定义搜索相关属性和方法，便于非层级关系组件间互相传值和调用（映射组件：SearchVideo）
export const useSearchStore = defineStore('search', () => {

    const keyword = ref('')
    const sortType = ref('')
    const searchResults = ref()

    // 分页组件相关信息
    const currentPage = ref(1)      // 当前页数
    const pages = ref(1)            // 总页数
    const size = ref(16)            // 每页的视频个数
    const total = ref(1)            // 总视频个数

    const setKeyword = value => {
        keyword.value = value
    }

    const searchVideos = async () => {
        const res = await searchVideosByKeyword(keyword.value, currentPage.value, size.value, sortType.value)
        if (res.success) {
            total.value = res.data.total
            pages.value = res.data.pages
            searchResults.value = res.data.list
            console.log(searchResults.value)
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error'
            })
            console.log(res)
        }
    }

    const changeVideosSortord = type => {
        sortType.value = type
        console.log(sortType.value)
        searchVideos()
    }

    const handelCurrentChange = page => {
        currentPage.value = page
        searchVideos()
        console.log(currentPage.value)
    }

    return {
        keyword,
        sortType,
        searchResults,
        currentPage,
        pages,
        size,
        total,
        setKeyword,
        searchVideos,
        changeVideosSortord,
        handelCurrentChange
    }
})