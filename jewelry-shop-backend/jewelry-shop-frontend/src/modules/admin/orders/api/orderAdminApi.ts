import { http } from '@/shared/api/http'
import type { AdminOrderDetail, AdminOrderPage, OrderStatusPayload } from '@/modules/admin/orders/types/order'

function buildQuery(params: Record<string, string | number | undefined>) {
  const search = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === '') {
      return
    }

    search.set(key, String(value))
  })

  const query = search.toString()
  return query ? `?${query}` : ''
}

export const orderAdminApi = {
  getAll(orderStatus = '', paymentStatus = '', q = '') {
    return http.get<AdminOrderPage>(
      `/api/admin/orders${buildQuery({
        page: 0,
        size: 100,
        sortBy: 'createdAt',
        sortDir: 'desc',
        q,
        orderStatus,
        paymentStatus,
      })}`,
    )
  },
  getById(id: number) {
    return http.get<AdminOrderDetail>(`/api/admin/orders/${id}`)
  },
  updateStatus(id: number, payload: OrderStatusPayload) {
    return http.patch<AdminOrderDetail>(`/api/admin/orders/${id}/status`, payload)
  },
}
