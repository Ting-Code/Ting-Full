import type { ApiResult, LoginData, LoginPayload, UserInfo } from '@ting/shared'
import http from './http'

export type { LoginData, LoginPayload, UserInfo }

export async function login(payload: LoginPayload) {
  const { data } = await http.post<ApiResult<LoginData>>('/user/login', payload)
  return data.data
}

export async function fetchMe() {
  const { data } = await http.get<ApiResult<UserInfo>>('/user/me')
  return data.data
}
