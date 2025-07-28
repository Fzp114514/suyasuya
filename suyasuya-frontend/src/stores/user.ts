

import { getUserInfo, updateAnnouncement, updateSignature } from "@/api/userInfo"
import { ElMessage } from "element-plus";
import { defineStore } from "pinia";
import { Ref, ref } from "vue";

export const useUserStore = defineStore('user', () => {
    interface UserInfo {
        [key: string]: any
    }
    const userInfo = ref<UserInfo>({
        userId: null,
        userName: null,
        gender: null,
        birthday: null,
        avatar: null,
        signature: null,
        announcement: null
    })
    const getUserId = () => userInfo.value.userId
    const getUserName = () => userInfo.value.userName
    const getUserBirthday = () => userInfo.value.birthday
    const getUserAvatar = () => userInfo.value.avatar
    const getSignature = () => userInfo.value.signature
    const getAnnouncement = () => userInfo.value.announcement
    const clearUserInfo = () => userInfo.value = {}

    // 异步操作

    const setUserInfo = (value: UserInfo) => userInfo.value = value

    const setSignature = async (signature: string) => {
        const res = await updateSignature(signature)
        console.log(res)
        if (res.success) {
            ElMessage({
                message: res.message,
                type: 'success'
            })
            userInfo.value.signature = signature
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error'
            })
        }
    }
    const setAnnouncement = async (announcement: string) => {
        const res = await updateAnnouncement(announcement)
        if (res.success) {
            userInfo.value.announcement = announcement
            ElMessage({
                message: res.message,
                type: 'success'
            })
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error'
            })
        }
    }

    // 更新用户信息（将数据库中的数据同步到仓库）
    const updateUserInfo = async () => {
        const res = await getUserInfo()
        if (res.success) {
            userInfo.value = res.data
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error'
            })
        }
    }

    return {
        userInfo,
        getUserId,
        getUserName,
        getUserBirthday,
        getUserAvatar,
        getSignature,
        getAnnouncement,
        setUserInfo,
        setSignature,
        setAnnouncement,
        updateUserInfo,
        clearUserInfo
    }
}, {
    persist: true
})