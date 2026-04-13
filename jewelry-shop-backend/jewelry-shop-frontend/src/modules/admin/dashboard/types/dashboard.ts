export interface RevenuePoint {
  label: string
  revenue: number
  orders: number
}

export interface RevenueStats {
  days: number
  totalRevenue: number
  totalPaidOrders: number
  averageOrderValue: number
  chart: RevenuePoint[]
}
