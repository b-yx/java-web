import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Request interceptor - attach token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('tlias_token')
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  error => Promise.reject(error)
)

// Response interceptor - unwrap result
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 1) {
      return res.data
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('tlias_token')
      localStorage.removeItem('tlias_user')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
