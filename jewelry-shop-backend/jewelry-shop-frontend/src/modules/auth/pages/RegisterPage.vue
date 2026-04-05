<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { authApi } from '@/modules/auth/api/authApi'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { useAuthStore } from '@/stores/auth/useAuthStore'
import { useCartStore } from '@/stores/cart/useCartStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const uiStore = useUiStore()

const form = reactive({
  fullName: '',
  email: '',
  password: '',
})

const errorMessage = ref('')
const isSubmitting = ref(false)

const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : null
})

async function handleSubmit() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const authSnapshot = await authApi.register(form)
    authStore.setAuth(authSnapshot, true)
    await cartStore.hydrateCart()
    uiStore.pushToast('Tạo tài khoản thành công', 'success')
    await router.push(redirectTarget.value ?? '/')
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tạo tài khoản'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="stack-md auth-page">
    <PageHero eyebrow="Tài khoản" title="Tạo tài khoản" description="Tạo tài khoản Vedora để mua hàng và theo dõi đơn." />
    <section class="surface-card form-card">
      <form class="stack-md" @submit.prevent="handleSubmit()">
        <label class="field">
          <span>Họ và tên</span>
          <input v-model.trim="form.fullName" type="text" required autocomplete="name" />
        </label>
        <label class="field">
          <span>Email</span>
          <input v-model.trim="form.email" type="email" required autocomplete="email" />
        </label>
        <label class="field">
          <span>Mật khẩu</span>
          <input v-model="form.password" type="password" required autocomplete="new-password" />
        </label>
        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
        <button class="primary-button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? 'Đang xử lý...' : 'Tạo tài khoản' }}
        </button>
      </form>
    </section>
  </div>
</template>
