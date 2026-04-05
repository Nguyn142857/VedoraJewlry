import { http } from '@/shared/api/http'
import type {
  AdminCategory,
  AdminCategoryPage,
  CategoryPayload,
  CategoryStatusPayload,
} from '@/modules/admin/categories/types/category'

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

export const categoryAdminApi = {
  getAll(q = '', status = '') {
    return http.get<AdminCategoryPage>(
      `/api/admin/categories${buildQuery({ page: 0, size: 100, sortBy: 'createdAt', sortDir: 'desc', q, status })}`,
    )
  },
  create(payload: CategoryPayload) {
    return http.post<AdminCategory>('/api/admin/categories', payload)
  },
  update(id: number, payload: CategoryPayload) {
    return http.put<AdminCategory>(`/api/admin/categories/${id}`, payload)
  },
  changeStatus(id: number, payload: CategoryStatusPayload) {
    return http.patch<AdminCategory>(`/api/admin/categories/${id}/status`, payload)
  },
  delete(id: number) {
    return http.delete<void>(`/api/admin/categories/${id}`)
  },
}
