<script setup lang="ts">
defineProps<{
  title: string
  description: string
}>()
</script>

<template>
  <section class="surface-card state-panel">
    <h2>{{ title }}</h2>
    <p class="page-copy">{{ description }}</p>
    <slot />
  </section>
</template>
