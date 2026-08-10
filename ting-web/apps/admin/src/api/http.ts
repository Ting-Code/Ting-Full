import axios from 'axios'
import { message } from 'antd'
import { AUTH_TOKEN_HEADER, type ApiResult } from '@ting/shared'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('ting_token')
  if (token) {
    config.headers[AUTH_TOKEN_HEADER] = token
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
    } else if (status === 403) {
      message.error(msg || '没有权限')
      return Promise.reject(error)
    }
    message.error(msg)
    return Promise.reject(error)
  },
)

export type { ApiResult }
export default http
