<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { catalogApi } from '@/modules/catalog/api/catalogApi'
import type { CategorySummary, ProductSummary } from '@/modules/catalog/types/catalog'
import { formatCurrency } from '@/shared/utils/format'

const categories = ref<CategorySummary[]>([])
const products = ref<ProductSummary[]>([])
const bestSellerProducts = ref<ProductSummary[]>([])
const selectedCategoryId = ref<number | null>(null)

const isLoadingProducts = ref(false)
const isLoadingCategories = ref(false)
const isLoadingBestSellers = ref(false)
const isFiltering = ref(false)
const errorMessage = ref('')

const currentSlide = ref(0)
let autoPlayInterval: ReturnType<typeof setInterval> | null = null

const slides = [
  {
    eyebrow: 'Vedora Jewelry • Edition 2026',
    title: 'Trang sức dành cho<br>những khoảnh khắc tinh tế',
    subtitle:
      'Khám phá những thiết kế hiện đại, tối giản và sang trọng được tạo ra để tôn lên dấu ấn riêng của bạn.',
    image:
      'https://cdn.s99.vn/ss1/prod/product/434b0dfb9d305b2690e258e0b44e2198.jpg',
    primaryText: 'Mua ngay',
    secondaryText: 'Khám phá bộ sưu tập',
  },
  {
    eyebrow: 'Luxury • Timeless • Personal',
    title: 'Nét đẹp thanh lịch<br>trong từng chi tiết',
    subtitle:
      'Vedora mang đến trải nghiệm trang sức cao cấp với tinh thần tối giản và cảm hứng đương đại.',
    image:
      'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=1200',
    primaryText: 'Xem sản phẩm',
    secondaryText: 'Xem lookbook',
  },
]

function stopAutoPlay() {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval)
    autoPlayInterval = null
  }
}

function startAutoPlay() {
  stopAutoPlay()
  autoPlayInterval = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.length
  }, 7000)
}

function nextSlide() {
  currentSlide.value = (currentSlide.value + 1) % slides.length
  startAutoPlay()
}

function prevSlide() {
  currentSlide.value = (currentSlide.value - 1 + slides.length) % slides.length
  startAutoPlay()
}

function goToSlide(index: number) {
  currentSlide.value = index
  startAutoPlay()
}

async function loadCategories() {
  isLoadingCategories.value = true
  try {
    const res = await catalogApi.getCategories()
    categories.value = res.content
  } catch (error) {
    console.error('Load categories failed:', error)
  } finally {
    isLoadingCategories.value = false
  }
}

async function loadProducts() {
  isLoadingProducts.value = true
  errorMessage.value = ''
  try {
    const res = await catalogApi.getProducts({
      page: 0,
      size: 8,
      sortBy: 'createdAt',
      sortDir: 'desc',
      categoryId: selectedCategoryId.value ?? undefined,
    })
    products.value = res.content
  } catch (error) {
    console.error('Load products failed:', error)
    errorMessage.value = 'Không thể tải sản phẩm'
  } finally {
    isLoadingProducts.value = false
  }
}

async function loadBestSellers() {
  isLoadingBestSellers.value = true
  try {
    bestSellerProducts.value = await catalogApi.getBestSellers(4)
  } catch (error) {
    console.error('Load best sellers failed:', error)
    bestSellerProducts.value = []
  } finally {
    isLoadingBestSellers.value = false
  }
}

async function loadInitialData() {
  await Promise.all([loadCategories(), loadProducts(), loadBestSellers()])
}

watch(selectedCategoryId, async () => {
  isFiltering.value = true
  await loadProducts()
  setTimeout(() => {
    isFiltering.value = false
  }, 260)
})

onMounted(() => {
  loadInitialData()
  startAutoPlay()
})

onUnmounted(() => {
  stopAutoPlay()
})
</script>

<template>
  <div class="lux-home">
    <section class="hero-shell">
      <div class="hero-slider">
        <article
          v-for="(slide, index) in slides"
          :key="index"
          class="hero-slide"
          :class="{ active: currentSlide === index }"
        >
          <img :src="slide.image" :alt="slide.eyebrow" class="hero-image" />
          <div class="hero-overlay"></div>

          <div class="hero-content container">
            <div class="hero-copy">
              <p class="hero-eyebrow">{{ slide.eyebrow }}</p>
              <h1 class="hero-title" v-html="slide.title"></h1>
              <p class="hero-subtitle">{{ slide.subtitle }}</p>

              <div class="hero-actions">
                <RouterLink to="/products" class="btn-primary-gold">
                  {{ slide.primaryText }}
                </RouterLink>
                <RouterLink to="/products" class="btn-secondary-light">
                  {{ slide.secondaryText }}
                </RouterLink>
              </div>
            </div>

            <div class="hero-floating-card">
              <span class="floating-tag">Vedora Signature</span>
              <h3>New Capsule Drop</h3>
              <p>Thiết kế nổi bật cho phong cách tối giản hiện đại.</p>
              <RouterLink to="/products" class="floating-link">Xem ngay</RouterLink>
            </div>
          </div>
        </article>

        <div class="hero-bottom container">
          <div class="hero-nav">
            <button type="button" class="nav-btn" @click="prevSlide">←</button>
            <button type="button" class="nav-btn" @click="nextSlide">→</button>
          </div>

          <div class="hero-dots">
            <button
              v-for="(_, i) in slides"
              :key="i"
              class="hero-dot"
              :class="{ active: currentSlide === i }"
              @click="goToSlide(i)"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="intro-strip container">
      <div class="intro-item">
        <span>Chất liệu cao cấp</span>
        <p>Thiết kế tinh xảo từ những chất liệu được tuyển chọn kỹ lưỡng.</p>
      </div>
      <div class="intro-item">
        <span>Phong cách hiện đại</span>
        <p>Tối giản, thanh lịch và phù hợp cho cả thường ngày lẫn dịp đặc biệt.</p>
      </div>
      <div class="intro-item">
        <span>Quà tặng ý nghĩa</span>
        <p>Mỗi món trang sức là một dấu ấn riêng dành cho người được trao gửi.</p>
      </div>
    </section>

    <section
      v-if="isLoadingBestSellers || bestSellerProducts.length > 0"
      class="section-block container"
    >
      <div class="section-head center">
        <p class="section-eyebrow">Top picks</p>
        <h2 class="section-title">Sản phẩm bán chạy</h2>
        <p class="section-desc">
          Những thiết kế được yêu thích nhất, được chọn để tôn lên vẻ đẹp tinh tế và hiện đại.
        </p>
      </div>

      <div v-if="isLoadingBestSellers" class="state-wrap">
        <div class="mini-loader"></div>
      </div>

      <div v-else class="featured-grid">
        <article
          v-for="product in bestSellerProducts"
          :key="product.id"
          class="featured-card"
        >
          <div class="featured-media">
            <img
              :src="product.thumbnail || 'https://via.placeholder.com/400'"
              :alt="product.name"
            />
            <div class="featured-overlay">
              <RouterLink :to="`/products/${product.slug}`" class="featured-btn">
                Xem chi tiết
              </RouterLink>
            </div>
          </div>

          <div class="featured-info">
            <span class="featured-category">{{ product.categoryName }}</span>
            <h3>{{ product.name }}</h3>
            <p>{{ formatCurrency(product.basePrice) }}</p>
          </div>
        </article>
      </div>
    </section>

    <section class="catalog-section container">
      <div class="section-head split">
        <div>
          <p class="section-eyebrow">New arrivals</p>
          <h2 class="section-title">Khám phá sản phẩm mới</h2>
          <p class="section-desc">
            Tuyển chọn những mẫu trang sức mới nhất dành cho phong cách thanh lịch, tối giản.
          </p>
        </div>

        <RouterLink to="/products" class="text-link">
          Xem tất cả
        </RouterLink>
      </div>

      <div class="filter-wrap">
        <div v-if="isLoadingCategories" class="state-inline">
          <div class="mini-loader"></div>
        </div>

        <div v-else class="filter-pills">
          <button
            type="button"
            class="filter-pill"
            :class="{ active: selectedCategoryId === null }"
            @click="selectedCategoryId = null"
          >
            Tất cả
          </button>

          <button
            v-for="cat in categories"
            :key="cat.id"
            type="button"
            class="filter-pill"
            :class="{ active: selectedCategoryId === cat.id }"
            @click="selectedCategoryId = cat.id"
          >
            {{ cat.name }}
          </button>
        </div>
      </div>

      <div
        class="catalog-grid-wrap"
        :class="{ filtering: isFiltering || isLoadingProducts }"
      >
        <div v-if="isLoadingProducts && !isFiltering" class="state-wrap">
          <div class="mini-loader"></div>
        </div>

        <div v-else-if="errorMessage" class="state-wrap error-text">
          {{ errorMessage }}
        </div>

        <div v-else-if="products.length === 0" class="state-wrap">
          Không tìm thấy sản phẩm nào phù hợp.
        </div>

        <div v-else class="catalog-grid">
          <article
            v-for="(product, index) in products"
            :key="product.id"
            class="catalog-card"
            :style="{ '--delay': `${index * 0.06}s` }"
          >
            <div class="catalog-media">
              <span v-if="index < 4 && selectedCategoryId === null" class="badge-soft">
                New
              </span>

              <img
                :src="product.thumbnail || 'https://via.placeholder.com/400'"
                :alt="product.name"
              />

              <div class="catalog-actions">
                <RouterLink :to="`/products/${product.slug}`" class="catalog-btn">
                  Xem chi tiết
                </RouterLink>
              </div>
            </div>

            <div class="catalog-info">
              <span class="catalog-category">{{ product.categoryName }}</span>
              <h3 class="catalog-title">{{ product.name }}</h3>
              <p class="catalog-price">{{ formatCurrency(product.basePrice) }}</p>
            </div>
          </article>
        </div>

        <div class="bottom-cta" v-if="products.length >= 8">
          <RouterLink to="/products" class="btn-outline-dark">
            Xem toàn bộ bộ sưu tập
          </RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Playfair+Display:wght@600;700&display=swap');

:global(body) {
  background: #f8f3ee;
}

.lux-home {
  min-height: 100vh;
  background:
    radial-gradient(circle at top, rgba(201, 166, 120, 0.08), transparent 28%),
    linear-gradient(180deg, #fbf7f2 0%, #f6efe8 100%);
  color: #201913;
  font-family: 'Inter', sans-serif;
}

.container {
  width: min(1320px, calc(100% - 32px));
  margin: 0 auto;
}

.hero-shell {
  padding: 18px 18px 0;
}

.hero-slider {
  position: relative;
  min-height: 86vh;
  border-radius: 32px;
  overflow: hidden;
  background: #0f0d0b;
  box-shadow: 0 24px 60px rgba(47, 31, 17, 0.16);
}

.hero-slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.9s ease;
}

.hero-slide.active {
  opacity: 1;
  z-index: 1;
}

.hero-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.03);
  filter: brightness(0.74);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(10, 8, 7, 0.78) 0%, rgba(10, 8, 7, 0.24) 46%, rgba(10, 8, 7, 0.08) 100%),
    linear-gradient(180deg, rgba(10, 8, 7, 0.1) 0%, rgba(10, 8, 7, 0.36) 100%);
}

.hero-content {
  position: relative;
  z-index: 2;
  min-height: 86vh;
  display: grid;
  grid-template-columns: 1.1fr 360px;
  align-items: center;
  gap: 24px;
}

.hero-copy {
  max-width: 680px;
  color: #fff;
  padding-top: 60px;
}

.hero-eyebrow {
  margin: 0 0 18px;
  color: #d7b58d;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.35em;
  text-transform: uppercase;
}

.hero-title {
  margin: 0;
  font-family: 'Playfair Display', serif;
  font-size: clamp(2.8rem, 6vw, 5.8rem);
  line-height: 0.95;
  letter-spacing: -0.03em;
}

.hero-subtitle {
  margin: 24px 0 0;
  max-width: 540px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 1.06rem;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 34px;
}

.btn-primary-gold,
.btn-secondary-light,
.btn-outline-dark,
.featured-btn,
.catalog-btn,
.floating-link,
.text-link {
  text-decoration: none;
}

.btn-primary-gold {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 170px;
  padding: 14px 22px;
  border-radius: 999px;
  background: linear-gradient(135deg, #d3aa76 0%, #a8793d 100%);
  color: #fff;
  font-weight: 700;
  box-shadow: 0 12px 26px rgba(168, 121, 61, 0.26);
}

.btn-secondary-light {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 190px;
  padding: 14px 22px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  color: #fff;
  backdrop-filter: blur(8px);
  background: rgba(255, 255, 255, 0.06);
  font-weight: 600;
}

.hero-floating-card {
  align-self: end;
  justify-self: end;
  width: 100%;
  max-width: 330px;
  margin-bottom: 92px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(14px);
  color: #fff;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.14);
}

.hero-floating-card h3 {
  margin: 10px 0 8px;
  font-family: 'Playfair Display', serif;
  font-size: 1.55rem;
}

.hero-floating-card p {
  margin: 0;
  color: rgba(255, 255, 255, 0.78);
  line-height: 1.7;
}

.floating-tag {
  display: inline-flex;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(211, 170, 118, 0.22);
  color: #f4d3ac;
  font-size: 0.72rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.14em;
}

.floating-link {
  display: inline-flex;
  margin-top: 16px;
  color: #fff;
  font-weight: 700;
}

.hero-bottom {
  position: absolute;
  z-index: 3;
  left: 50%;
  bottom: 28px;
  transform: translateX(-50%);
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hero-nav {
  display: flex;
  gap: 10px;
}

.nav-btn {
  width: 48px;
  height: 48px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-size: 1.1rem;
  cursor: pointer;
  backdrop-filter: blur(10px);
}

.hero-dots {
  display: flex;
  gap: 10px;
}

.hero-dot {
  width: 10px;
  height: 10px;
  border: 0;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.38);
  cursor: pointer;
  transition: all 0.25s ease;
}

.hero-dot.active {
  width: 34px;
  background: #fff;
}

.intro-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-top: 26px;
}

.intro-item {
  padding: 24px 22px;
  border-radius: 24px;
  border: 1px solid #ece1d6;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 10px 24px rgba(66, 41, 22, 0.04);
}

.intro-item span {
  display: block;
  margin-bottom: 10px;
  color: #8c6741;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.75rem;
}

.intro-item p {
  margin: 0;
  color: #5f5146;
  line-height: 1.7;
}

.section-block,
.catalog-section {
  padding: 78px 0 24px;
}

.section-head {
  margin-bottom: 26px;
}

.section-head.center {
  max-width: 760px;
  margin-inline: auto;
  text-align: center;
}

.section-head.split {
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 24px;
}

.section-eyebrow {
  margin: 0 0 10px;
  color: #9c7853;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.28em;
  text-transform: uppercase;
}

.section-title {
  margin: 0;
  font-family: 'Playfair Display', serif;
  font-size: clamp(2rem, 4vw, 3.2rem);
  line-height: 1.05;
  letter-spacing: -0.03em;
  color: #221a14;
}

.section-desc {
  max-width: 680px;
  margin: 14px 0 0;
  color: #76685c;
  line-height: 1.8;
}

.text-link {
  color: #1f1711;
  font-weight: 700;
  white-space: nowrap;
  border-bottom: 1px solid #1f1711;
  padding-bottom: 3px;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22px;
}

.featured-card,
.catalog-card {
  transition: transform 0.3s ease;
}

.featured-card:hover,
.catalog-card:hover {
  transform: translateY(-4px);
}

.featured-media,
.catalog-media {
  position: relative;
  overflow: hidden;
  border-radius: 26px;
  background: #efe8e1;
  aspect-ratio: 3 / 4;
}

.featured-media img,
.catalog-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s ease;
}

.featured-card:hover img,
.catalog-card:hover img {
  transform: scale(1.06);
}

.featured-overlay,
.catalog-actions {
  position: absolute;
  inset-inline: 18px;
  bottom: 18px;
  opacity: 0;
  transform: translateY(12px);
  transition: all 0.28s ease;
}

.featured-card:hover .featured-overlay,
.catalog-card:hover .catalog-actions {
  opacity: 1;
  transform: translateY(0);
}

.featured-btn,
.catalog-btn {
  display: inline-flex;
  justify-content: center;
  width: 100%;
  padding: 13px 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.94);
  color: #241b14;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.12);
}

.featured-info,
.catalog-info {
  padding: 16px 8px 4px;
  text-align: center;
}

.featured-category,
.catalog-category {
  display: inline-block;
  color: #a1784a;
  font-size: 0.72rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.16em;
}

.featured-info h3,
.catalog-title {
  margin: 10px 0 8px;
  font-size: 1.08rem;
  font-weight: 600;
  color: #241b14;
}

.featured-info p,
.catalog-price {
  margin: 0;
  font-weight: 700;
  color: #2f2319;
}

.filter-wrap {
  margin: 32px 0 28px;
}

.filter-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-pill {
  border: 1px solid #e5d7ca;
  background: rgba(255, 255, 255, 0.82);
  color: #7f6b57;
  border-radius: 999px;
  padding: 12px 18px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.25s ease;
}

.filter-pill.active {
  background: linear-gradient(135deg, #d7b07c 0%, #b6864c 100%);
  border-color: transparent;
  color: #fff;
  box-shadow: 0 10px 22px rgba(182, 134, 76, 0.22);
}

.catalog-grid-wrap {
  transition: opacity 0.28s ease, transform 0.28s ease;
}

.catalog-grid-wrap.filtering {
  opacity: 0.5;
  transform: translateY(8px);
  pointer-events: none;
}

.catalog-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 26px 20px;
}

.catalog-card {
  opacity: 0;
  animation: riseIn 0.55s ease forwards;
  animation-delay: var(--delay);
}

.badge-soft {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 2;
  padding: 7px 12px;
  border-radius: 999px;
  background: #1f1711;
  color: #fff;
  font-size: 0.68rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.bottom-cta {
  text-align: center;
  margin-top: 42px;
}

.btn-outline-dark {
  display: inline-flex;
  justify-content: center;
  padding: 14px 28px;
  border-radius: 999px;
  border: 1px solid #241b14;
  color: #241b14;
  font-weight: 700;
  transition: all 0.25s ease;
}

.btn-outline-dark:hover {
  background: #241b14;
  color: #fff;
}

.state-wrap,
.state-inline {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
}

.error-text {
  color: #c45243;
  font-weight: 600;
}

.mini-loader {
  width: 26px;
  height: 26px;
  border: 2px solid #eadfd3;
  border-top-color: #b6864c;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes riseIn {
  from {
    opacity: 0;
    transform: translateY(22px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1200px) {
  .hero-content {
    grid-template-columns: 1fr;
  }

  .hero-floating-card {
    display: none;
  }

  .featured-grid,
  .catalog-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 860px) {
  .intro-strip {
    grid-template-columns: 1fr;
  }

  .section-head.split {
    flex-direction: column;
    align-items: start;
  }

  .featured-grid,
  .catalog-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-slider {
    min-height: 78vh;
  }

  .hero-content {
    min-height: 78vh;
  }

  .hero-bottom {
    bottom: 20px;
  }
}

@media (max-width: 560px) {
  .container {
    width: min(1320px, calc(100% - 20px));
  }

  .hero-shell {
    padding: 10px 10px 0;
  }

  .hero-slider {
    border-radius: 24px;
    min-height: 74vh;
  }

  .hero-content {
    min-height: 74vh;
  }

  .hero-copy {
    padding-top: 24px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .featured-grid,
  .catalog-grid {
    grid-template-columns: 1fr;
  }

  .filter-pills {
    gap: 10px;
  }

  .filter-pill {
    width: 100%;
    text-align: center;
  }
}
</style>
