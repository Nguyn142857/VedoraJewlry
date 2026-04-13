import { http } from '@/shared/api/http'
import type {
  AdminAccount,
  AdminAccountPage,
  AccountPayload,
} from '@/modules/admin/accounts/types/account'

function buildQuery(params: Record<string, string | number | boolean | undefined>) {
  const search = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === '') return
    search.set(key, String(value))
  })

  const query = search.toString()
  return query ? `?${query}` : ''
}

const BASE_URL = '/api/admin/accounts'

export const accountAdminApi = {
  getAll(params?: {
    page?: number
    size?: number
    q?: string
    status?: string | boolean
    sortBy?: string
    sortDir?: 'asc' | 'desc'
  }) {
    return http.get<AdminAccountPage>(
      `${BASE_URL}${buildQuery({
        page: params?.page ?? 0,
        size: params?.size ?? 100,
        sortBy: params?.sortBy ?? 'createdAt',
        sortDir: params?.sortDir ?? 'desc',
        q: params?.q ?? '',
        status: params?.status,
      })}`,
    )
  },

  getById(id: number) {
    return http.get<AdminAccount>(`${BASE_URL}/${id}`)
  },

  create(payload: AccountPayload) {
    return http.post<AdminAccount>(BASE_URL, payload)
  },

  update(id: number, payload: AccountPayload) {
    return http.put<AdminAccount>(`${BASE_URL}/${id}`, payload)
  },

  delete(id: number) {
    return http.delete<void>(`${BASE_URL}/${id}`)
  },

  getRoles() {
    return http.get<string[]>('/api/admin/roles')
  },
}
