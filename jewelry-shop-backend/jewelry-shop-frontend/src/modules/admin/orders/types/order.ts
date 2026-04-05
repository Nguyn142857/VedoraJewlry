import type { PageResponse } from '@/shared/types/api'

export interface AdminOrderSummary {
  id: number
  orderCode: string
  finalAmount: number
  paymentStatus: 'UNPAID' | 'PAID'
  orderStatus: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'
  receiverName: string
  receiverPhone: string
  createdAt: string
}

export interface AdminOrderItem {
  id: number
  productVariantId: number
  productName: string
  variantInfo: string | null
  quantity: number
  unitPrice: number
  subtotal: number
}

export interface AdminOrderDetail {
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
  items: AdminOrderItem[]
  createdAt: string
  updatedAt: string
}

export interface OrderStatusPayload {
  orderStatus?: AdminOrderDetail['orderStatus']
  paymentStatus?: AdminOrderDetail['paymentStatus']
}

export type AdminOrderPage = PageResponse<AdminOrderSummary>
