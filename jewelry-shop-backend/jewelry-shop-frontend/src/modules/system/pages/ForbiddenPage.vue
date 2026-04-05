<script setup lang="ts">
import { RouterLink } from 'vue-router'

import StatePanel from '@/shared/components/feedback/StatePanel.vue'
</script>

<template>
  <StatePanel title="Không có quyền truy cập" description="Tài khoản hiện tại không được phép sử dụng khu vực hoặc chức năng này.">
    <RouterLink class="primary-button inline-button" to="/">Quay lại trang chủ</RouterLink>
  </StatePanel>
</template>
