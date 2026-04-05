import type { PageResponse } from '@/shared/types/api'

export interface AdminProduct {
  id: number
  name: string
  slug: string
  description: string | null
  material: string | null
  basePrice: number
  thumbnail: string | null
  status: boolean
  categoryId: number
  categoryName: string
  createdAt: string
  updatedAt: string
}

export interface ProductPayload {
  name: string
  slug: string
  description: string
  material: string
  basePrice: number
  thumbnail: string
  status: boolean
  categoryId: number
}

export type AdminProductPage = PageResponse<AdminProduct>
