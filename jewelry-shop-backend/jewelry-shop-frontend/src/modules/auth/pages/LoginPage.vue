<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'

import { authApi } from '@/modules/auth/api/authApi'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { env } from '@/shared/constants/env'
import { useAuthStore } from '@/stores/auth/useAuthStore'
import { useCartStore } from '@/stores/cart/useCartStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const uiStore = useUiStore()

const form = reactive({
  email: '',
  password: '',
  rememberMe: true,
})

const errorMessage = ref('')
const isSubmitting = ref(false)
const isGoogleSubmitting = ref(false)
const googleButtonRef = ref<HTMLDivElement | null>(null)

const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : null
})

const isGoogleLoginEnabled = computed(() => Boolean(env.googleClientId))

async function completeAuth(authSnapshot: Awaited<ReturnType<typeof authApi.login>>) {
  authStore.setAuth(authSnapshot, form.rememberMe)
  await cartStore.hydrateCart()
  uiStore.pushToast('Đăng nhập thành công', 'success')
  await router.push(redirectTarget.value ?? (authStore.isAdmin ? '/admin' : '/'))
}

async function handleSubmit() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const authSnapshot = await authApi.login({
      email: form.email,
      password: form.password,
    })
    await completeAuth(authSnapshot)
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể đăng nhập'
  } finally {
    isSubmitting.value = false
  }
}

async function handleGoogleLogin(idToken: string) {
  errorMessage.value = ''
  isGoogleSubmitting.value = true

  try {
    const authSnapshot = await authApi.googleLogin({ idToken })
    await completeAuth(authSnapshot)
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể đăng nhập với Google'
  } finally {
    isGoogleSubmitting.value = false
  }
}

function loadGoogleScript() {
  return new Promise<void>((resolve, reject) => {
    const existingScript = document.getElementById('google-identity-script')
    if (existingScript) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.id = 'google-identity-script'
    script.src = 'https://accounts.google.com/gsi/client'
    script.async = true
    script.defer = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Failed to load Google Identity Services'))
    document.head.appendChild(script)
  })
}

async function initializeGoogleLogin() {
  if (!isGoogleLoginEnabled.value || !googleButtonRef.value) {
    return
  }

  try {
    await loadGoogleScript()

    const googleApi = (window as Window & { google?: any }).google?.accounts?.id
    if (!googleApi) {
      throw new Error('Google API unavailable')
    }

    googleApi.initialize({
      client_id: env.googleClientId,
      callback: async (response: { credential?: string }) => {
        if (!response.credential) {
          uiStore.pushToast('Không nhận được Google ID token', 'error')
          return
        }

        await handleGoogleLogin(response.credential)
      },
    })

    googleApi.renderButton(googleButtonRef.value, {
      theme: 'outline',
      size: 'large',
      shape: 'pill',
      text: 'continue_with',
      width: 320,
      logo_alignment: 'left',
    })
  } catch {
    uiStore.pushToast('Không thể tải đăng nhập Google', 'error')
  }
}

onMounted(async () => {
  await initializeGoogleLogin()
})
</script>

<template>
  <div class="auth-page auth-centered">
    <section class="auth-panel">
      <PageHero title="Đăng nhập" description="Đăng nhập để quản lý giỏ hàng, tài khoản và đơn hàng." />

      <section class="surface-card form-card auth-form-card">
        <form class="stack-md" @submit.prevent="handleSubmit()">
          <label class="field">
            <span>Email</span>
            <input v-model.trim="form.email" type="email" required autocomplete="email" />
          </label>
          <label class="field">
            <span>Mật khẩu</span>
            <input v-model="form.password" type="password" required autocomplete="current-password" />
          </label>

          <div class="auth-form-meta">
            <label class="checkbox-inline">
              <input v-model="form.rememberMe" type="checkbox" />
              <span>Nhớ mật khẩu</span>
            </label>
            <RouterLink class="auth-link" :to="{ path: '/forgot-password', query: redirectTarget ? { redirect: redirectTarget } : {} }">
              Quên mật khẩu?
            </RouterLink>
          </div>

          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

          <button class="primary-button auth-submit" type="submit" :disabled="isSubmitting || isGoogleSubmitting">
            {{ isSubmitting ? 'Đang xử lý...' : 'Đăng nhập' }}
          </button>
        </form>

        <template v-if="isGoogleLoginEnabled">
          <div class="auth-divider">
            <span>hoặc</span>
          </div>

          <div class="google-login-block">
            <div ref="googleButtonRef" class="google-button-host" />
            <p v-if="isGoogleSubmitting" class="muted-helper">Đang xử lý đăng nhập Google...</p>
          </div>
        </template>

        <p class="auth-switch">
          Chưa có tài khoản?
          <RouterLink class="auth-link" :to="{ path: '/register', query: redirectTarget ? { redirect: redirectTarget } : {} }">
            Đăng ký
          </RouterLink>
        </p>
      </section>
    </section>
  </div>
</template>
