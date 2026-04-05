import { http } from '@/shared/api/http'
import type { AuthSnapshot } from '@/modules/auth/types/auth'

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  fullName: string
  email: string
  password: string
}

export interface GoogleLoginPayload {
  idToken: string
}

export const authApi = {
  login(payload: LoginPayload) {
    return http.post<AuthSnapshot>('/api/auth/login', payload)
  },
  register(payload: RegisterPayload) {
    return http.post<AuthSnapshot>('/api/auth/register', payload)
  },
  googleLogin(payload: GoogleLoginPayload) {
    return http.post<AuthSnapshot>('/api/auth/google', payload)
  },
}
