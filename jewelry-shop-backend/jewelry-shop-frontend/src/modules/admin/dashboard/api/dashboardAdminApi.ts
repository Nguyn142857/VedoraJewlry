import { http } from '@/shared/api/http'
import type { RevenueStats } from '@/modules/admin/dashboard/types/dashboard'

export const dashboardAdminApi = {
  getRevenueStats(days = 7) {
    return http.get<RevenueStats>(`/api/admin/dashboard/revenue?days=${days}`)
  },
}
