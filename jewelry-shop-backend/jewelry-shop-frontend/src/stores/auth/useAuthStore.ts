import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import type { AuthSnapshot } from '@/modules/auth/types/auth'
import { profileApi } from '@/modules/profile/api/profileApi'
import { isJwtExpired } from '@/shared/utils/auth'
import { getStoredAuthSnapshot, setStoredAuthSnapshot } from '@/shared/utils/storage'

export const useAuthStore = defineStore('auth', () => {
  const auth = ref<AuthSnapshot | null>(null)
  const authResolved = ref(false)

  const isAuthenticated = computed(() => Boolean(auth.value?.token))
  const userRoles = computed(() => auth.value?.roles ?? [])
  const isAdmin = computed(() => userRoles.value.includes('ROLE_ADMIN'))
  const displayName = computed(() => auth.value?.fullName ?? 'Guest')

  function hydrate() {
    auth.value = getStoredAuthSnapshot()
  }

  async function initialize() {
    authResolved.value = false
    hydrate()

    if (!auth.value?.token || isJwtExpired(auth.value.token)) {
      clearAuth()
      authResolved.value = true
      return
    }

    try {
      const profile = await profileApi.getMyProfile()
      auth.value = {
        ...(auth.value as AuthSnapshot),
        fullName: profile.fullName,
        email: profile.email,
        roles: profile.roles,
      }
      setStoredAuthSnapshot(auth.value, true)
    } catch {
      clearAuth()
    } finally {
      authResolved.value = true
    }
  }

  function setAuth(snapshot: AuthSnapshot, rememberMe = true) {
    auth.value = snapshot
    setStoredAuthSnapshot(snapshot, rememberMe)
    authResolved.value = true
  }

  function clearAuth() {
    auth.value = null
    setStoredAuthSnapshot(null)
  }

  return {
    auth,
    authResolved,
    displayName,
    isAdmin,
    isAuthenticated,
    userRoles,
    initialize,
    hydrate,
    setAuth,
    clearAuth,
  }
})
