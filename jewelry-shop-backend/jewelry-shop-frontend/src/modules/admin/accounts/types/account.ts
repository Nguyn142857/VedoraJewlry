export interface AdminAccount {
  id: number
  fullName: string
  email: string
  phone?: string
  address?: string
  status: boolean
  roles: string[]
  createdAt?: string
  updatedAt?: string
}

export interface AdminAccountPage {
  content: AdminAccount[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface AccountPayload {
  fullName: string
  email: string
  phone?: string
  address?: string
  password?: string
  status: boolean
  roles: string[]
}
