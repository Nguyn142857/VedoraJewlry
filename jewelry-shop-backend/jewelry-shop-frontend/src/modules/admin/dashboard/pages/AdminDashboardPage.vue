<script setup lang="ts">
import PageHero from '@/shared/components/base/PageHero.vue'
</script>

<template>
  <div class="stack-md">
    <PageHero
      eyebrow="Admin"
      title="Dashboard"
      description="Khu admin foundation da san sang, du backend dashboard hien tai moi co endpoint placeholder."
    />
    <section class="card-grid">
      <article class="feature-card">
        <h2>Categories</h2>
        <p>CRUD categories se gan vao bang va form o sprint tiep theo.</p>
      </article>
      <article class="feature-card">
        <h2>Products</h2>
        <p>Danh sach, form product va bo loc admin se duoc gan tiep theo.</p>
      </article>
      <article class="feature-card">
        <h2>Orders</h2>
        <p>Trang order admin se ho tro xem chi tiet va update statuses.</p>
      </article>
    </section>
  </div>
</template>
