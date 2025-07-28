import { Ref, ref } from 'vue'
import { defineStore } from 'pinia'

export const useDisplayStore = defineStore('display', () => {

  // 登录方式（是否为邮箱登录）
  const isUseCaptchaLogin = ref<boolean>(false)

  // 主页组件显示模式
  const isShow = ref<boolean>(true)

  // 个人空间页列表显示模式
  const isCubeList = ref<boolean>(true)

  // 是否开启弹幕（video页面代码待修改）
  const isOpenDanmaku = ref(true)

  const changeLoginMode = (): boolean => isUseCaptchaLogin.value ? isUseCaptchaLogin.value = false : isUseCaptchaLogin.value = true
  const changeHomeDisplayState = (): boolean => isShow.value ? isShow.value = false : isShow.value = true
  const changeListDisplayState = (): boolean => isCubeList.value ? isCubeList.value = false : isCubeList.value = true
  const changeDanmakuDisplayState = (): boolean => isOpenDanmaku.value ? isOpenDanmaku.value = false : isOpenDanmaku.value = true

  return {
    isShow,
    isCubeList,
    isOpenDanmaku,
    isUseCaptchaLogin,
    changeHomeDisplayState,
    changeListDisplayState,
    changeDanmakuDisplayState,
    changeLoginMode
  }
}, {
  // 启用本地存储
  persist: true
})
