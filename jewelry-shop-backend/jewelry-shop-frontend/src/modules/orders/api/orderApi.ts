import { http } from '@/shared/api/http'
import type { OrderCreatePayload, UserOrderDetail, UserOrderPage } from '@/modules/orders/types/order'

export const orderApi = {
  getMyOrders() {
    return http.get<UserOrderPage>('/api/user/orders?page=0&size=100&sortBy=createdAt&sortDir=desc')
  },
  getMyOrderDetail(id: number) {
    return http.get<UserOrderDetail>(`/api/user/orders/${id}`)
  },
  createOrder(payload: OrderCreatePayload) {
    return http.post<UserOrderDetail>('/api/user/orders', payload)
  },
}
