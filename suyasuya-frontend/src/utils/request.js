/* 封装axios用于发送请求 */
import axios from 'axios'
import { useLoadingStore } from '@/stores/loading'
import router from '@/router'

const loadingStore = useLoadingStore()
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const isDebug = import.meta.env.VITE_DEBUG_MODE

// 创建一个新的axios实例
const request = axios.create({
  baseURL: `${apiBaseUrl}/api`,         // 基地址
  timeout: isDebug ? 5000 : 10000       // 响应时间
})

// 添加请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}` // 确保格式正确
  }
  loadingStore.show()   // 开启loading动画，禁止背景点击（节流，防止多次无效触发）
  return config

}, error => {
  return Promise.reject(error)
})

// 添加响应拦截器
request.interceptors.response.use(response => {
  loadingStore.hide()
  return response.data
}, error => {
  const response = error.response
  loadingStore.hide()
  if (response && response.status === 401) {
    alert(`认证已过期，请重新登录`)
    localStorage.removeItem('token')
    router.push('/login')
  }
  return Promise.reject(error)
})

export default request