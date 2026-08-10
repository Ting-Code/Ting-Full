import http, { type ApiResult } from './http'

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginData {
  token: string
  userId: number
  username: string
  nickname: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  status: number
}

export async function login(payload: LoginPayload) {
  const { data } = await http.post<ApiResult<LoginData>>('/user/login', payload)
  return data.data
}

export async function fetchMe() {
  const { data } = await http.get<ApiResult<UserInfo>>('/user/me')
  return data.data
}
