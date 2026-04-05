<script setup lang="ts">
import { RouterLink } from 'vue-router'

import PageHero from '@/shared/components/base/PageHero.vue'
</script>

<template>
  <div class="auth-page auth-centered">
    <section class="auth-panel">
      <PageHero
        eyebrow="Hỗ trợ"
        title="Quên mật khẩu"
        description="Chức năng đặt lại mật khẩu đang được hoàn thiện. Bạn có thể liên hệ quản trị viên để được cấp lại mật khẩu."
      />

      <section class="surface-card form-card auth-form-card stack-md">
        <p class="page-copy">
          Tạm thời hãy dùng tài khoản quản trị hoặc liên hệ bộ phận hỗ trợ để được đặt lại mật khẩu cho tài khoản của bạn.
        </p>
        <a class="ghost-button inline-button" href="mailto:admin@vedora.local">Liên hệ hỗ trợ</a>
        <RouterLink class="primary-button inline-button" to="/login">Quay lại đăng nhập</RouterLink>
      </section>
    </section>
  </div>
</template>
