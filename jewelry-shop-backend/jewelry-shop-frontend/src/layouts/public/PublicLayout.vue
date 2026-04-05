<script setup lang="ts">
import { RouterView } from 'vue-router'

import AppHeader from '@/shared/components/navigation/AppHeader.vue'
import AppToastHost from '@/shared/components/feedback/AppToastHost.vue'
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <main class="page-shell">
      <RouterView />
    </main>
    <footer class="site-footer">
      <p>Vedora Jewelry. Tiền bạc có thể giúp chúng ta mua được sự thoải mái về vật chất. Tuy nhiên chúng không thể giúp mua được sự an bình trong tâm hồn.</p>
    </footer>
    <AppToastHost />
  </div>
</template>
