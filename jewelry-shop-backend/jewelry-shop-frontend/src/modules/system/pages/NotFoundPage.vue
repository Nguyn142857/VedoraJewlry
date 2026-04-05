<script setup lang="ts">
import { RouterLink } from 'vue-router'

import StatePanel from '@/shared/components/feedback/StatePanel.vue'
</script>

<template>
  <StatePanel title="Không tìm thấy trang" description="Liên kết này không còn tồn tại hoặc bạn đã nhập sai địa chỉ.">
    <RouterLink class="primary-button inline-button" to="/">Về trang chủ</RouterLink>
  </StatePanel>
</template>
