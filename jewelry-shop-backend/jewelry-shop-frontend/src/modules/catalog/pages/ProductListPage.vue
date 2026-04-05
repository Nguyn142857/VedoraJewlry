<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { catalogApi } from '@/modules/catalog/api/catalogApi'
import type { CategorySummary, ProductSummary } from '@/modules/catalog/types/catalog'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'

const route = useRoute()
const router = useRouter()

const categories = ref<CategorySummary[]>([])
const products = ref<ProductSummary[]>([])
const totalPages = ref(0)
const currentPage = ref(0)
const totalElements = ref(0)
const isLoading = ref(false)

const filters = reactive({
  q: '',
  categoryId: '',
})

const pageTitle = computed(() => `Sản phẩm (${totalElements.value})`)

async function loadCategories() {
  const response = await catalogApi.getCategories()
  categories.value = response.content
}

async function loadProducts() {
  isLoading.value = true

  try {
    const response = await catalogApi.getProducts({
      page: Number(route.query.page ?? 0),
      size: 12,
      q: String(route.query.q ?? ''),
      categoryId: route.query.categoryId ? Number(route.query.categoryId) : undefined,
    })

    products.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
    currentPage.value = response.page
  } finally {
    isLoading.value = false
  }
}

function syncFiltersFromRoute() {
  filters.q = String(route.query.q ?? '')
  filters.categoryId = String(route.query.categoryId ?? '')
}

async function applyFilters(page = 0) {
  await router.push({
    path: '/products',
    query: {
      page: page > 0 ? String(page) : undefined,
      q: filters.q || undefined,
      categoryId: filters.categoryId || undefined,
    },
  })
}

watch(
  () => route.query,
  async () => {
    syncFiltersFromRoute()
    await loadProducts()
  },
)

onMounted(async () => {
  syncFiltersFromRoute()
  await Promise.all([loadCategories(), loadProducts()])
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Bộ sưu tập" :title="pageTitle" description="Khám phá bộ sưu tập trang sức đang mở bán." />

    <section class="surface-card">
      <form class="filter-grid" @submit.prevent="applyFilters()">
        <label class="field">
          <span>Tìm kiếm</span>
          <input v-model.trim="filters.q" type="text" placeholder="Tìm theo tên sản phẩm" />
        </label>
        <label class="field">
          <span>Danh mục</span>
          <select v-model="filters.categoryId">
            <option value="">Tất cả danh mục</option>
            <option v-for="category in categories" :key="category.id" :value="String(category.id)">
              {{ category.name }}
            </option>
          </select>
        </label>
        <button class="primary-button align-end" type="submit">Lọc sản phẩm</button>
      </form>
    </section>

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải sản phẩm...</p>
    </section>

    <section v-else class="product-grid">
      <article v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <img v-if="product.thumbnail" :src="product.thumbnail" :alt="product.name" />
          <div v-else class="image-fallback">Vedora</div>
        </div>
        <div class="stack-md">
          <div>
            <p class="muted-label">{{ product.categoryName }}</p>
            <h2 class="product-title">{{ product.name }}</h2>
            <p class="price-text">{{ formatCurrency(product.basePrice) }}</p>
          </div>
          <RouterLink class="primary-button inline-button" :to="`/products/${product.slug}`">Xem chi tiết</RouterLink>
        </div>
      </article>
    </section>

    <section v-if="!isLoading && products.length === 0" class="surface-card">
      <p>Không có sản phẩm phù hợp với bộ lọc hiện tại.</p>
    </section>

    <section v-if="totalPages > 1" class="pagination-row">
      <button class="ghost-button" type="button" :disabled="currentPage === 0" @click="applyFilters(currentPage - 1)">
        Trang trước
      </button>
      <span>Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
      <button
        class="ghost-button"
        type="button"
        :disabled="currentPage >= totalPages - 1"
        @click="applyFilters(currentPage + 1)"
      >
        Trang sau
      </button>
    </section>
  </div>
</template>
