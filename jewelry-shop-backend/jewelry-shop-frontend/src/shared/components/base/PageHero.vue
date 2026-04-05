<script setup lang="ts">
defineProps<{
  eyebrow?: string
  title: string
  description: string
}>()
</script>

<template>
  <section class="page-hero">
    <p v-if="eyebrow" class="eyebrow">{{ eyebrow }}</p>
    <h1>{{ title }}</h1>
    <p class="page-copy">{{ description }}</p>
  </section>
</template>
