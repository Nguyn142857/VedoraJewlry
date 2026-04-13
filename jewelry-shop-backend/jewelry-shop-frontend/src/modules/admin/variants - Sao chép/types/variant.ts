import type { PageResponse } from '@/shared/types/api'

export interface AdminVariant {
  id: number
  productId: number
  productName: string
  productSlug: string
  sku: string
  size: string | null
  color: string | null
  gemstone: string | null
  price: number
  stockQuantity: number
  status: boolean
  deleted: boolean
  createdAt: string
  updatedAt: string
}

export interface VariantPayload {
  productId: number
  sku: string
  size: string
  color: string
  gemstone: string
  price: number
  stockQuantity: number
  status: boolean
}

export interface VariantUpdatePayload {
  sku: string
  size: string
  color: string
  gemstone: string
  price: number
  stockQuantity: number
  status: boolean
}

export interface VariantStatusPayload {
  status: boolean
}

export type AdminVariantPage = PageResponse<AdminVariant>
