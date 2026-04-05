import type { PageResponse } from '@/shared/types/api'

export interface AdminCategory {
  id: number
  name: string
  slug: string
  description: string | null
  status: 'ACTIVE' | 'INACTIVE' | 'DELETED'
  createdAt: string
  updatedAt: string
}

export interface CategoryPayload {
  name: string
  slug: string
  description: string
}

export interface CategoryStatusPayload {
  status: 'ACTIVE' | 'INACTIVE'
}

export type AdminCategoryPage = PageResponse<AdminCategory>
