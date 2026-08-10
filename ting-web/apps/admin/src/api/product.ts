import type { ApiResult, Product, ProductSavePayload } from '@ting/shared'
import http from './http'

export type { Product, ProductSavePayload }

export async function fetchProducts() {
  const { data } = await http.get<ApiResult<Product[]>>('/biz/products')
  return data.data
}

export async function createProduct(payload: ProductSavePayload) {
  const { data } = await http.post<ApiResult<Product>>('/biz/products', payload)
  return data.data
}

export async function updateProduct(id: number, payload: ProductSavePayload) {
  const { data } = await http.put<ApiResult<Product>>(`/biz/products/${id}`, payload)
  return data.data
}

export async function deleteProduct(id: number) {
  await http.delete<ApiResult<null>>(`/biz/products/${id}`)
}
