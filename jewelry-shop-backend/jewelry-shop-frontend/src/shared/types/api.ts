export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export interface ErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  validationErrors: Record<string, string> | null
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}
