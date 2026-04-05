import { http } from '@/shared/api/http'
import type { CartResponse } from '@/modules/cart/types/cart'

export interface AddCartItemPayload {
  productVariantId: number
  quantity: number
}

export interface UpdateCartItemPayload {
  quantity: number
}

export const cartApi = {
  getMyCart() {
    return http.get<CartResponse>('/api/user/cart')
  },
  addItem(payload: AddCartItemPayload) {
    return http.post<CartResponse>('/api/user/cart/items', payload)
  },
  updateItem(itemId: number, payload: UpdateCartItemPayload) {
    return http.put<CartResponse>(`/api/user/cart/items/${itemId}`, payload)
  },
  removeItem(itemId: number) {
    return http.delete<CartResponse>(`/api/user/cart/items/${itemId}`)
  },
  clearCart() {
    return http.delete<CartResponse>('/api/user/cart/items')
  },
}
