import type { AuthSnapshot } from '@/modules/auth/types/auth'

const AUTH_STORAGE_KEY = 'vedora.auth'

type PersistenceMode = 'local' | 'session'

function parseStoredSnapshot(rawValue: string | null, mode: PersistenceMode) {
  if (!rawValue) {
    return null
  }

  try {
    return JSON.parse(rawValue) as AuthSnapshot
  } catch {
    if (mode === 'local') {
      localStorage.removeItem(AUTH_STORAGE_KEY)
    } else {
      sessionStorage.removeItem(AUTH_STORAGE_KEY)
    }
    return null
  }
}

export function getStoredAuthSnapshot(): AuthSnapshot | null {
  return (
    parseStoredSnapshot(sessionStorage.getItem(AUTH_STORAGE_KEY), 'session') ??
    parseStoredSnapshot(localStorage.getItem(AUTH_STORAGE_KEY), 'local')
  )
}

export function setStoredAuthSnapshot(snapshot: AuthSnapshot | null, rememberMe = true) {
  if (!snapshot) {
    localStorage.removeItem(AUTH_STORAGE_KEY)
    sessionStorage.removeItem(AUTH_STORAGE_KEY)
    return
  }

  const serializedSnapshot = JSON.stringify(snapshot)

  if (rememberMe) {
    localStorage.setItem(AUTH_STORAGE_KEY, serializedSnapshot)
    sessionStorage.removeItem(AUTH_STORAGE_KEY)
    return
  }

  sessionStorage.setItem(AUTH_STORAGE_KEY, serializedSnapshot)
  localStorage.removeItem(AUTH_STORAGE_KEY)
}
