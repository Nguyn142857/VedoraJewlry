<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'

import AppHeader from '@/shared/components/navigation/AppHeader.vue'
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <main class="panel-shell">
      <aside class="panel-nav">
        <h2>My Account</h2>
        <RouterLink to="/account/profile">Profile</RouterLink>
        <RouterLink to="/account/orders">Orders</RouterLink>
      </aside>
      <section class="panel-content">
        <RouterView />
      </section>
    </main>
  </div>
</template>
