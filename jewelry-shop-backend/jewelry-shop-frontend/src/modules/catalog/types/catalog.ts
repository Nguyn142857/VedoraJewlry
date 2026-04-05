import type { PageResponse } from '@/shared/types/api'

export interface CategorySummary {
  id: number
  name: string
  slug: string
}

export interface ProductSummary {
  id: number
  name: string
  slug: string
  basePrice: number
  thumbnail: string | null
  status: boolean
  categoryId: number
  categoryName: string
}

export interface ProductDetail {
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

export interface ProductVariantSummary {
  id: number
  sku: string
  size: string | null
  color: string | null
  gemstone: string | null
  price: number
  stockQuantity: number
  status: boolean
}

export interface ProductImageSummary {
  id: number
  imageUrl: string
  isMain: boolean
  sortOrder: number | null
}

export interface ProductListFilters {
  page?: number
  size?: number
  q?: string
  categoryId?: number
  minPrice?: number
  maxPrice?: number
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}

export type CategoryPage = PageResponse<CategorySummary>
export type ProductPage = PageResponse<ProductSummary>
export type VariantPage = PageResponse<ProductVariantSummary>
export type ProductImagePage = PageResponse<ProductImageSummary>
