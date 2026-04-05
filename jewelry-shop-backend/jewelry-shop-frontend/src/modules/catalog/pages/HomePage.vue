<script setup lang="ts">
import PageHero from '@/shared/components/base/PageHero.vue'
</script>

<template>
  <div class="stack-lg">
    <PageHero
      eyebrow="Storefront"
      title="Vedora Jewelry"
      description="Giai doan 1 da san sang: app shell, router, stores va API foundation da duoc khoi tao."
    />

    <section class="card-grid">
      <article class="feature-card">
        <h2>Catalog foundation</h2>
        <p>Danh muc, danh sach san pham, chi tiet san pham se duoc lap day o sprint tiep theo.</p>
      </article>
      <article class="feature-card">
        <h2>Account flow</h2>
        <p>Dang nhap, profile, cart va orders da co route va state skeleton.</p>
      </article>
      <article class="feature-card">
        <h2>Admin shell</h2>
        <p>Khu quan tri da co layout rieng de gan CRUD categories, products, variants, images va orders.</p>
      </article>
    </section>
  </div>
</template>
