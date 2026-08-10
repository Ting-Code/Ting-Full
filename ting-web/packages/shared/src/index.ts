export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginData {
  token: string
  userId: number
  username: string
  nickname: string
  roles: string[]
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  status: number
  roles: string[]
}

export interface Product {
  id: number
  name: string
  price: number
  stock: number
  status: number
  createdAt?: string | number[]
  updatedAt?: string | number[]
}

export interface ProductSavePayload {
  name: string
  price: number
  stock: number
  status?: number
}

/** 与后端约定的请求头 */
export const AUTH_TOKEN_HEADER = 'X-Token'

export const ROLE_ADMIN = 'ADMIN'
export const ROLE_USER = 'USER'
