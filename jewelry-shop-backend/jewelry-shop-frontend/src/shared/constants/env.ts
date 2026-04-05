const apiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim()
const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID?.trim() || null

if (!apiBaseUrl) {
  throw new Error('Missing VITE_API_BASE_URL in frontend environment')
}

export const env = {
  apiBaseUrl,
  googleClientId,
}
