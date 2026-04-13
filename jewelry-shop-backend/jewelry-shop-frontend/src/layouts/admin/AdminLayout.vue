<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'

import AppHeader from '@/shared/components/navigation/AppHeader.vue'
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <main class="panel-shell admin-shell">
      <aside class="panel-nav">
        <h2>Admin</h2>
        <RouterLink to="/admin">Dashboard</RouterLink>
        <RouterLink to="/admin/accounts">Accounts</RouterLink>
        <RouterLink to="/admin/categories">Categories</RouterLink>
        <RouterLink to="/admin/products">Products</RouterLink>
        <RouterLink to="/admin/variants">Variants</RouterLink>
        <RouterLink to="/admin/images">Images</RouterLink>
        <RouterLink to="/admin/orders">Orders</RouterLink>
      </aside>
      <section class="panel-content">
        <RouterView />
      </section>
    </main>
  </div>
</template>
