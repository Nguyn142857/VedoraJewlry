import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'

import { pinia } from '@/app/pinia'
import { useAuthStore } from '@/stores/auth/useAuthStore'

function redirectToLogin(to: RouteLocationNormalized, next: NavigationGuardNext) {
  next({
    path: '/login',
    query: {
      redirect: to.fullPath,
    },
  })
}

export function requireAuth(
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext,
) {
  const authStore = useAuthStore(pinia)

  if (!authStore.isAuthenticated) {
    redirectToLogin(to, next)
    return
  }

  next()
}

export function requireAdmin(
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext,
) {
  const authStore = useAuthStore(pinia)

  if (!authStore.isAuthenticated) {
    redirectToLogin(to, next)
    return
  }

  if (!authStore.isAdmin) {
    next('/forbidden')
    return
  }

  next()
}
