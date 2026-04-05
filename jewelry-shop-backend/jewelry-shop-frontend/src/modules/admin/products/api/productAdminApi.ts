import { http } from '@/shared/api/http'
import type { AdminProduct, AdminProductPage, ProductPayload } from '@/modules/admin/products/types/product'

function buildQuery(params: Record<string, string | number | boolean | undefined>) {
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

export const productAdminApi = {
  getAll(q = '', categoryId = '', status = '') {
    return http.get<AdminProductPage>(
      `/api/admin/products${buildQuery({
        page: 0,
        size: 100,
        sortBy: 'createdAt',
        sortDir: 'desc',
        q,
        categoryId,
        status,
      })}`,
    )
  },
  create(payload: ProductPayload) {
    return http.post<AdminProduct>('/api/admin/products', payload)
  },
  update(id: number, payload: ProductPayload) {
    return http.put<AdminProduct>(`/api/admin/products/${id}`, payload)
  },
  delete(id: number) {
    return http.delete<void>(`/api/admin/products/${id}`)
  },
}
