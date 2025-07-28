import { defineStore } from "pinia";
import { Ref, ref } from "vue";

// 管理所有的搜索关键词
export const usekeywordsStore = defineStore('keywords', () => {
    const keywords = ref<Array<string>>([])
    const addKeyword = (keyword: string): number => keywords.value.push(keyword)
    const removeKeywordByIndex = (index: number): string[] => keywords.value.splice(index, 1)
    const removeKeywordByValue = (value: string): string[] => keywords.value = keywords.value.filter(item => item !== value)
    const findKeyword = (value: string): boolean => keywords.value.includes(value)
    const clearKeywords = (): Array<null> => keywords.value = []
    const getKeywordsLength = (): number => keywords.value.length

    // 还需要定义一个初始化搜索历史的方法（异步操作,根据用户id获取搜索历史）
    // 还需定义一个上传关键词的方法（异步操作）

    return {
        keywords,
        addKeyword,
        removeKeywordByIndex,
        removeKeywordByValue,
        findKeyword,
        clearKeywords,
        getKeywordsLength
    }
}, {
    persist: true
})