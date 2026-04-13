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
  minPrice: '',
  maxPrice: '',
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
      minPrice: route.query.minPrice ? Number(route.query.minPrice) : undefined,
      maxPrice: route.query.maxPrice ? Number(route.query.maxPrice) : undefined,
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
  filters.minPrice = String(route.query.minPrice ?? '')
  filters.maxPrice = String(route.query.maxPrice ?? '')
}

async function applyFilters(page = 0) {
  const min = filters.minPrice ? Number(filters.minPrice) : undefined
  const max = filters.maxPrice ? Number(filters.maxPrice) : undefined

  if (min !== undefined && max !== undefined && min > max) {
    alert('Giá từ không được lớn hơn giá đến')
    return
  }

  await router.push({
    path: '/products',
    query: {
      page: page > 0 ? String(page) : undefined,
      q: filters.q || undefined,
      categoryId: filters.categoryId || undefined,
      minPrice: filters.minPrice || undefined,
      maxPrice: filters.maxPrice || undefined,
    },
  })
}

async function resetFilters() {
  filters.q = ''
  filters.categoryId = ''
  filters.minPrice = ''
  filters.maxPrice = ''

  await router.push({
    path: '/products',
    query: {},
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
    <PageHero
      eyebrow="Bộ sưu tập"
      :title="pageTitle"
      description="Khám phá bộ sưu tập trang sức đang mở bán."
    />

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
            <option
              v-for="category in categories"
              :key="category.id"
              :value="String(category.id)"
            >
              {{ category.name }}
            </option>
          </select>
        </label>

        <label class="field">
          <span>Giá từ</span>
          <input
            v-model="filters.minPrice"
            type="number"
            min="0"
            step="1000"
            placeholder="Ví dụ: 500000"
          />
        </label>

        <label class="field">
          <span>Giá đến</span>
          <input
            v-model="filters.maxPrice"
            type="number"
            min="0"
            step="1000"
            placeholder="Ví dụ: 2000000"
          />
        </label>

        <div class="filter-actions">
          <button class="primary-button" type="submit">Lọc sản phẩm</button>
          <button class="ghost-button" type="button" @click="resetFilters">Xóa bộ lọc</button>
        </div>
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

          <RouterLink class="primary-button inline-button" :to="`/products/${product.slug}`">
            Xem chi tiết
          </RouterLink>
        </div>
      </article>
    </section>

    <section v-if="!isLoading && products.length === 0" class="surface-card">
      <p>Không có sản phẩm phù hợp với bộ lọc hiện tại.</p>
    </section>

    <section v-if="totalPages > 1" class="pagination-row">
      <button
        class="ghost-button"
        type="button"
        :disabled="currentPage === 0"
        @click="applyFilters(currentPage - 1)"
      >
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
<style scoped>
.filter-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 1rem;
  align-items: end;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.field span {
  font-size: 0.9rem;
  font-weight: 600;
}

.field input,
.field select {
  height: 44px;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 0 0.875rem;
  outline: none;
  background: #fff;
}

.field input:focus,
.field select:focus {
  border-color: #111;
}

.filter-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  flex-wrap: wrap;
}

@media (max-width: 1024px) {
  .filter-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
}
</style>
