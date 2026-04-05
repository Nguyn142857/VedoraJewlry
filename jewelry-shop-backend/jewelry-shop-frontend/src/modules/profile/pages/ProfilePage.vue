<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { profileApi } from '@/modules/profile/api/profileApi'
import type { UserProfile } from '@/modules/profile/types/profile'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { useAuthStore } from '@/stores/auth/useAuthStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const authStore = useAuthStore()
const uiStore = useUiStore()

const profile = ref<UserProfile | null>(null)
const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')

const form = reactive({
  fullName: '',
  phone: '',
  address: '',
})

async function loadProfile() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await profileApi.getMyProfile()
    profile.value = response
    form.fullName = response.fullName
    form.phone = response.phone ?? ''
    form.address = response.address ?? ''
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải hồ sơ'
  } finally {
    isLoading.value = false
  }
}

async function handleSubmit() {
  isSaving.value = true
  errorMessage.value = ''

  try {
    const response = await profileApi.updateMyProfile(form)
    profile.value = response
    if (authStore.auth) {
      authStore.setAuth({
        ...authStore.auth,
        fullName: response.fullName,
        email: response.email,
        roles: response.roles,
      })
    }
    uiStore.pushToast('Đã cập nhật hồ sơ', 'success')
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể cập nhật hồ sơ'
  } finally {
    isSaving.value = false
  }
}

onMounted(async () => {
  await loadProfile()
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Tài khoản" title="Hồ sơ cá nhân" description="Cập nhật thông tin liên hệ để đặt hàng nhanh hơn." />

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải hồ sơ...</p>
    </section>

    <section v-else class="surface-card stack-md form-card">
      <p v-if="profile"><strong>Email:</strong> {{ profile.email }}</p>

      <form class="stack-md" @submit.prevent="handleSubmit()">
        <label class="field">
          <span>Họ và tên</span>
          <input v-model.trim="form.fullName" type="text" required />
        </label>
        <label class="field">
          <span>Số điện thoại</span>
          <input v-model.trim="form.phone" type="text" />
        </label>
        <label class="field">
          <span>Địa chỉ</span>
          <textarea v-model.trim="form.address" rows="4" />
        </label>
        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
        <button class="primary-button" type="submit" :disabled="isSaving">
          {{ isSaving ? 'Đang lưu...' : 'Lưu thay đổi' }}
        </button>
      </form>
    </section>
  </div>
</template>
