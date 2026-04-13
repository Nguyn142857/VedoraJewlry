import { http } from '@/shared/api/http'
import type {
  AdminVariant,
  AdminVariantPage,
  VariantPayload,
  VariantStatusPayload,
  VariantUpdatePayload,
} from '@/modules/admin/variants/types/variant'

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

export const variantAdminApi = {
  getAll(q = '', productId = '', status = '') {
    return http.get<AdminVariantPage>(
      `/api/admin/product-variants${buildQuery({
        page: 0,
        size: 100,
        sortBy: 'createdAt',
        sortDir: 'desc',
        q,
        productId,
        status,
      })}`,
    )
  },
  create(payload: VariantPayload) {
    return http.post<AdminVariant>('/api/admin/product-variants', payload)
  },
  update(id: number, payload: VariantUpdatePayload) {
    return http.put<AdminVariant>(`/api/admin/product-variants/${id}`, payload)
  },
  changeStatus(id: number, payload: VariantStatusPayload) {
    return http.patch<AdminVariant>(`/api/admin/product-variants/${id}/status`, payload)
  },
  delete(id: number) {
    return http.delete<void>(`/api/admin/product-variants/${id}`)
  },
}
