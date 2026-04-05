export interface CartItem {
  id: number
  productVariantId: number
  productId: number
  productName: string
  productSlug: string
  sku: string
  size: string | null
  color: string | null
  gemstone: string | null
  quantity: number
  availableStock: number
  unitPrice: number
  subtotal: number
}

export interface CartResponse {
  cartId: number
  userId: number
  items: CartItem[]
  totalAmount: number
  createdAt: string
  updatedAt: string
}
