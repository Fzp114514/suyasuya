/* 封装axios用于发送请求 */
import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useLoadingStore } from '@/stores/loading'
import router from '@/router'

const loadingStore = useLoadingStore()

export interface ApiResult<T = any> {
  success: boolean                        // 请求是否成功
  message: string                         // 请求消息
  data: T                                 // 返回的数据（泛型）
  timestamp: string                       // 响应时间戳（前端接收为字符串）
}

// 扩展AxiosRequestConfig以支持自定义配置
interface CustomRequestConfig extends AxiosRequestConfig {
  // 可添加自定义配置项
}

// 创建Axios实例
const createAxiosInstance = (): AxiosInstance => {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL as string
  const isDebug = import.meta.env.VITE_DEBUG_MODE === 'true'

  const instance = axios.create({
    baseURL: `${apiBaseUrl}/api`,
    timeout: isDebug ? 5000 : 10000
  })

  // 请求拦截器
  instance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    loadingStore.show()

    return config
  }, error => {
    return Promise.reject(error)
  })

  // 响应拦截器
  instance.interceptors.response.use((response: AxiosResponse) => {
    loadingStore.hide()

    return {
      ...response,
      data: {
        success: true,
        message: response.data.message || '请求成功',
        data: response.data.data,
        timestamp: response.data.timestamp || new Date().toISOString()
      }
    }
  }, (error: AxiosError) => {
    loadingStore.hide()

    if (error.response) {
      const status = error.response.status

      // 401 认证过期处理
      if (status === 401) {
        alert('认证已过期，请重新登录')
        localStorage.removeItem('token')
        router.push('/login')
        return Promise.reject({
          success: false,
          message: '认证已过期',
          data: null,
          timestamp: new Date().toISOString()
        })
      }

      // 其他错误返回统一结构
      const apiError: ApiResult = {
        success: false,
        message: (error.response.data as any)?.message || `请求错误 (${status})`,
        data: null,
        timestamp: new Date().toISOString()
      }

      return Promise.reject(apiError)
    }

    // 网络错误或超时
    const networkError: ApiResult = {
      success: false,
      message: error.message || '网络错误，请检查连接',
      data: null,
      timestamp: new Date().toISOString()
    }
    return Promise.reject(networkError)
  })

  return instance
}

// 创建请求实例
const request = createAxiosInstance()

// 封装请求函数以提供更好的类型支持
export const apiRequest = {
  get: async <T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResult<T>> => {
    const response = await request.get<T, AxiosResponse<ApiResult<T>>>(url, config)
    return response.data
  },

  post: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResult<T>> => {
    const response = await request.post<T, AxiosResponse<ApiResult<T>>>(url, data, config)
    return response.data
  },

  put: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResult<T>> => {
    const response = await request.put<T, AxiosResponse<ApiResult<T>>>(url, data, config)
    return response.data
  },

  delete: async <T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResult<T>> => {
    const response = await request.delete<T, AxiosResponse<ApiResult<T>>>(url, config)
    return response.data
  }
}

export default apiRequest