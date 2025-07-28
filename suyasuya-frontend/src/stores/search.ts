import { searchVideosByKeyword } from "@/api/search"
import { defineStore } from "pinia"
import { Ref, ref } from "vue"
import { ElMessage } from "element-plus"

// 此仓库用于定义搜索相关属性和方法，便于非层级关系组件间互相传值和调用（映射组件：SearchVideo）
export const useSearchStore = defineStore('search', () => {

    const keyword = ref<string>('')
    const sortType = ref<string>('')
    const searchResults = ref()

    // 分页组件相关信息
    const currentPage = ref<number>(1)      // 当前页数
    const pages = ref<number>(1)            // 总页数
    const size = ref<number>(16)            // 每页的视频个数
    const total = ref<number>(1)            // 总视频个数

    const setKeyword = (value: string) => {
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

    const changeVideosSortord = (type: string) => {
        sortType.value = type
        console.log(sortType.value)
        searchVideos()
    }

    const handelCurrentChange = (page: number) => {
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