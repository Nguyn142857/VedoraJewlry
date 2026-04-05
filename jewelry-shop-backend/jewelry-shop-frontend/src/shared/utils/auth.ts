export function decodeJwtPayload(token: string): Record<string, unknown> | null {
  const parts = token.split('.')
  if (parts.length < 2) {
    return null
  }

  try {
    const encodedPayload = parts[1]
    if (!encodedPayload) {
      return null
    }

    const base64 = encodedPayload.replace(/-/g, '+').replace(/_/g, '/')
    const normalized = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=')
    const json = atob(normalized)
    return JSON.parse(json) as Record<string, unknown>
  } catch {
    return null
  }
}

export function isJwtExpired(token: string) {
  const payload = decodeJwtPayload(token)
  const exp = payload?.exp

  if (typeof exp !== 'number') {
    return true
  }

  return exp * 1000 <= Date.now()
}
