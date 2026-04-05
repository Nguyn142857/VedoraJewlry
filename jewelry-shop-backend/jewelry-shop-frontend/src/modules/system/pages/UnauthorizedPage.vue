<script setup lang="ts">
import { RouterLink } from 'vue-router'

import StatePanel from '@/shared/components/feedback/StatePanel.vue'
</script>

<template>
  <StatePanel title="Cần đăng nhập" description="Bạn cần đăng nhập để tiếp tục truy cập nội dung này.">
    <div class="action-row">
      <RouterLink class="primary-button inline-button" to="/login">Đăng nhập</RouterLink>
      <RouterLink class="ghost-button" to="/register">Tạo tài khoản</RouterLink>
    </div>
  </StatePanel>
</template>
