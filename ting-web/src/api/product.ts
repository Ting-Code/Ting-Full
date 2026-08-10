import http, { type ApiResult } from './http'

export interface Product {
  id: number
  name: string
  price: number
  stock: number
  status: number
  createdAt?: string | number[]
  updatedAt?: string | number[]
}

export async function fetchProducts() {
  const { data } = await http.get<ApiResult<Product[]>>('/biz/products')
  return data.data
}
