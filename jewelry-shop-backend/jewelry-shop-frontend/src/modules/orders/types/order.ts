import type { PageResponse } from '@/shared/types/api'

export interface UserOrderSummary {
  id: number
  orderCode: string
  finalAmount: number
  paymentStatus: 'UNPAID' | 'PAID'
  orderStatus: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'
  receiverName: string
  receiverPhone: string
  createdAt: string
}

export interface UserOrderItem {
  id: number
  productVariantId: number
  productName: string
  variantInfo: string | null
  quantity: number
  unitPrice: number
  subtotal: number
}

export interface UserOrderDetail {
  id: number
  orderCode: string
  userId: number
  userEmail: string
  totalAmount: number
  shippingFee: number
  discountAmount: number
  finalAmount: number
  paymentMethod: 'COD' | 'BANKING'
  paymentStatus: 'UNPAID' | 'PAID'
  orderStatus: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  note: string | null
  items: UserOrderItem[]
  createdAt: string
  updatedAt: string
}

export interface OrderCreatePayload {
  paymentMethod: 'COD' | 'BANKING'
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  note: string
}

export type UserOrderPage = PageResponse<UserOrderSummary>
