<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { RouterLink, useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth/useAuthStore'
import { useCartStore } from '@/stores/cart/useCartStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const authStore = useAuthStore()
const cartStore = useCartStore()
const uiStore = useUiStore()
const router = useRouter()

const { displayName, isAdmin, isAuthenticated } = storeToRefs(authStore)
const { itemCount } = storeToRefs(cartStore)

async function handleLogout() {
  authStore.clearAuth()
  cartStore.setItemCount(0)
  uiStore.pushToast('Đã đăng xuất', 'success')
  await router.replace('/login')
}
</script>

<template>
  <header class="site-header">
    <RouterLink class="brand-mark" to="/">Vedora</RouterLink>

    <nav class="site-nav">
      <RouterLink to="/">Trang chủ</RouterLink>
      <RouterLink to="/products">Sản phẩm</RouterLink>
      <RouterLink to="/cart">Giỏ hàng ({{ itemCount }})</RouterLink>
      <RouterLink v-if="isAuthenticated" to="/account/profile">Tài khoản</RouterLink>
      <RouterLink v-else to="/login">Đăng nhập</RouterLink>
      <RouterLink v-if="isAdmin" to="/admin">Admin</RouterLink>
    </nav>

    <div class="site-actions">
      <span class="muted-label">{{ displayName }}</span>
      <button v-if="isAuthenticated" class="ghost-button" type="button" @click="handleLogout()">Đăng xuất</button>
      <button class="ghost-button" type="button" @click="uiStore.toggleMobileNav()">Menu</button>
    </div>
  </header>
</template>
