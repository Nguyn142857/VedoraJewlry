import { createApp } from 'vue'

import App from './App.vue'
import { pinia } from './app/pinia'
import router from './router'
import { registerHttpHandlers } from './shared/api/http'
import { useAuthStore } from './stores/auth/useAuthStore'
import { useCartStore } from './stores/cart/useCartStore'
import { useUiStore } from './stores/ui/useUiStore'
import './shared/styles/main.css'

async function bootstrap() {
  const app = createApp(App)
  const authStore = useAuthStore(pinia)
  const cartStore = useCartStore(pinia)
  const uiStore = useUiStore(pinia)

  registerHttpHandlers({
    onUnauthorized: () => {
      authStore.clearAuth()
      cartStore.setItemCount(0)
      if (router.currentRoute.value.path !== '/login') {
        void router.replace({
          path: '/login',
          query: {
            redirect: router.currentRoute.value.fullPath,
          },
        })
      }
      uiStore.pushToast('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại', 'error')
    },
    onForbidden: () => {
      if (router.currentRoute.value.path.startsWith('/admin')) {
        void router.replace('/')
      }
      uiStore.pushToast('Bạn không có quyền truy cập chức năng này', 'error')
    },
  })

  await authStore.initialize()

  if (authStore.isAuthenticated) {
    await cartStore.hydrateCart()
  }

  app.use(pinia)
  app.use(router)

  await router.isReady()
  app.mount('#app')
}

void bootstrap()
