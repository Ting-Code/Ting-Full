import axios from 'axios'
import { message } from 'antd'

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('ting_token')
  if (token) {
    config.headers['X-Token'] = token
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult<unknown>
    if (body && typeof body.code === 'number' && body.code !== 0) {
      message.error(body.message || '请求失败')
      return Promise.reject(body)
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    const msg = error.response?.data?.message || error.message || '网络错误'
    if (status === 401) {
      localStorage.removeItem('ting_token')
      localStorage.removeItem('ting_user')
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`
      }
    }
    message.error(msg)
    return Promise.reject(error)
  },
)

export default http
