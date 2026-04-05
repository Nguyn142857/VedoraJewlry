import type { PageResponse } from '@/shared/types/api'

export interface AdminImage {
  id: number
  productId: number
  productName: string
  productSlug: string
  imageUrl: string
  isMain: boolean
  deleted: boolean
  sortOrder: number | null
  createdAt: string
  updatedAt: string
}

export interface ImagePayload {
  productId: number
  imageUrl: string
  isMain: boolean
  sortOrder: number
}

export interface ImageUpdatePayload {
  imageUrl: string
  isMain: boolean
  sortOrder: number
}

export type AdminImagePage = PageResponse<AdminImage>
