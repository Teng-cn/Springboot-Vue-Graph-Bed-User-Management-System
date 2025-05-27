import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    console.log('请求URL:', config.url)
    
    // 登录请求不需要token
    if (config.url === '/login') {
      console.log('登录请求，不添加Authorization头')
      return config
    }
    
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 为请求头添加token
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('添加Authorization头:', `Bearer ${token}`)
      
      // 打印完整请求头以进行调试
      console.log('完整请求头:', JSON.stringify(config.headers))
    } else {
      console.log('未找到token，请求未添加Authorization头')
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口有问题，需要处理错误
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: Token过期或未登录
      if (res.code === 401) {
        // 清除token
        localStorage.removeItem('token')
        // 跳转到登录页
        router.replace({
          path: '/login',
          query: { redirect: router.currentRoute.value.fullPath }
        })
      }
      
      return Promise.reject(new Error(res.message || '系统错误'))
    } else {
      return res
    }
  },
  (error) => {
    ElMessage({
      message: error.message || '请求错误',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

// 封装GET请求
export function get<T>(url: string, params?: object): Promise<T> {
  return service.get(url, { params })
}

// 封装POST请求
export function post<T>(url: string, data?: object): Promise<T> {
  return service.post(url, data)
}

// 封装PUT请求
export function put<T>(url: string, data?: object): Promise<T> {
  return service.put(url, data)
}

// 封装DELETE请求
export function del<T>(url: string, params?: object): Promise<T> {
  return service.delete(url, { params })
}

// 导出axios实例
export default service 