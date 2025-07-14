/* 封装axios用于发送请求 */
import axios from 'axios'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const isDebug = import.meta.env.VITE_DEBUG_MODE

// 创建一个新的axios实例
const request = axios.create({
  baseURL: `${apiBaseUrl}/api`,         // 基地址
  timeout: isDebug ? 5000 : 10000       // 响应时间
})

// 添加请求拦截器
request.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}` // 确保格式正确
  }
  return config
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error)
})

// 添加响应拦截器
request.interceptors.response.use(function (response) {
  // 对响应数据做点什么(默认axios会多包装一层data，此处减少数据层级)
  return response.data
}, function (error) {
  // 对响应错误做点什么
  return Promise.reject(error)
})

export default request