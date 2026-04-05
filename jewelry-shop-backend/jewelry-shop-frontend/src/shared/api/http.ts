import axios, { AxiosError, type AxiosRequestConfig } from 'axios'

import { env } from '@/shared/constants/env'
import { getStoredAuthSnapshot } from '@/shared/utils/storage'
import type { ApiResponse, ErrorResponse } from '@/shared/types/api'

export class HttpError extends Error {
  status: number
  payload: ErrorResponse | null

  constructor(message: string, status: number, payload: ErrorResponse | null = null) {
    super(message)
    this.name = 'HttpError'
    this.status = status
    this.payload = payload
  }
}

type RequestOptions = Omit<AxiosRequestConfig, 'url' | 'method' | 'data'>

type HttpHandlers = {
  onUnauthorized?: () => void
  onForbidden?: () => void
}

type RequestConfigWithAuthToken = AxiosRequestConfig & {
  _authToken?: string
}

const httpHandlers: HttpHandlers = {}

const httpClient = axios.create({
  baseURL: env.apiBaseUrl,
  headers: {
    Accept: 'application/json',
  },
})

httpClient.interceptors.request.use((config) => {
  const authSnapshot = getStoredAuthSnapshot()
  const requestConfig = config as RequestConfigWithAuthToken

  if (authSnapshot?.token) {
    config.headers.Authorization = `${authSnapshot.tokenType} ${authSnapshot.token}`
    requestConfig._authToken = authSnapshot.token
  }

  return config
})

async function request<T>(method: AxiosRequestConfig['method'], path: string, data?: unknown, options?: RequestOptions) {
  try {
    const response = await httpClient.request<ApiResponse<T>>({
      ...options,
      url: path,
      method,
      data,
    })

    return response.data.data
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    const payload = axiosError.response?.data ?? null
    const status = axiosError.response?.status ?? 500
    const currentToken = getStoredAuthSnapshot()?.token ?? null
    const requestToken = (axiosError.config as RequestConfigWithAuthToken | undefined)?._authToken ?? null
    const hasAuthSession = Boolean(currentToken)
    const isAuthEndpoint = path.startsWith('/api/auth/')
    const isCurrentSessionRequest = !requestToken || requestToken === currentToken

    if (status === 401 && hasAuthSession && isCurrentSessionRequest && !isAuthEndpoint) {
      httpHandlers.onUnauthorized?.()
    }

    if (status === 403 && hasAuthSession && isCurrentSessionRequest) {
      httpHandlers.onForbidden?.()
    }

    throw new HttpError(payload?.message ?? axiosError.message ?? 'Request failed', status, payload)
  }
}

export function registerHttpHandlers(handlers: HttpHandlers) {
  httpHandlers.onUnauthorized = handlers.onUnauthorized
  httpHandlers.onForbidden = handlers.onForbidden
}

export const http = {
  get: <T>(path: string, options?: RequestOptions) => request<T>('GET', path, undefined, options),
  post: <T>(path: string, body?: unknown, options?: RequestOptions) => request<T>('POST', path, body, options),
  put: <T>(path: string, body?: unknown, options?: RequestOptions) => request<T>('PUT', path, body, options),
  patch: <T>(path: string, body?: unknown, options?: RequestOptions) => request<T>('PATCH', path, body, options),
  delete: <T>(path: string, options?: RequestOptions) => request<T>('DELETE', path, undefined, options),
}
