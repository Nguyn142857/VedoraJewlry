export interface AuthSnapshot {
  token: string
  tokenType: string
  userId: number
  fullName: string
  email: string
  roles: string[]
}
