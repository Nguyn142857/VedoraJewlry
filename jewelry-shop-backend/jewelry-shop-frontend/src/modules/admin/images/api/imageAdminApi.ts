import { http } from '@/shared/api/http'
import type { AdminImage, AdminImagePage, ImagePayload, ImageUpdatePayload } from '@/modules/admin/images/types/image'

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

export const imageAdminApi = {
  getAll(productId = '', isMain = '') {
    return http.get<AdminImagePage>(
      `/api/admin/product-images${buildQuery({
        page: 0,
        size: 100,
        sortBy: 'createdAt',
        sortDir: 'desc',
        productId,
        isMain,
      })}`,
    )
  },
  create(payload: ImagePayload) {
    return http.post<AdminImage>('/api/admin/product-images', payload)
  },
  update(id: number, payload: ImageUpdatePayload) {
    return http.put<AdminImage>(`/api/admin/product-images/${id}`, payload)
  },
  delete(id: number) {
    return http.delete<void>(`/api/admin/product-images/${id}`)
  },
}
