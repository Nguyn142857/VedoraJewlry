<script setup lang="ts">
import { storeToRefs } from 'pinia'

import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()
const { toasts } = storeToRefs(uiStore)
</script>

<template>
  <div class="toast-host" aria-live="polite">
    <article
      v-for="toast in toasts"
      :key="toast.id"
      class="toast-card"
      :data-tone="toast.tone"
    >
      {{ toast.title }}
    </article>
  </div>
</template>
