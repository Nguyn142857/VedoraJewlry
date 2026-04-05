export interface UserProfile {
  id: number
  fullName: string
  email: string
  phone: string | null
  address: string | null
  status: string
  roles: string[]
  createdAt: string
  updatedAt: string
}

export interface UserProfilePayload {
  fullName: string
  phone: string
  address: string
}
