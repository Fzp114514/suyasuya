import { getUserInfo, updateAnnouncement, updateSignature } from "@/api/userInfo";
import { ElMessage } from "element-plus";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore('user', () => {
    // const userInfo = ref({
    //     userId: 114514,
    //     userName: '时与空',
    //     gender: '保密',
    //     birthday: '07-22',
    //     avatar: '../../../public/imgs/avatar/avatar1.jpg',
    //     signature: '起风了，唯有努力生存。',
    //     announcement: '苟利星奏生死以，岂因新岛避趋之。'
    // })
    const userInfo = ref({})
    const getUserId = () => userInfo.value.userId
    const getUserName = () => userInfo.value.userName
    const getUserBirthday = () => userInfo.value.birthday
    const getUserAvatar = () => userInfo.value.avatar
    const getSignature = () => userInfo.value.signature
    const getAnnouncement = () => userInfo.value.announcement
    const clearUserInfo = () => userInfo.value = null

    // 异步操作

    const setUserInfo = value => userInfo.value = value

    const setSignature = async signature => {
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
    const setAnnouncement = async announcement => {
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