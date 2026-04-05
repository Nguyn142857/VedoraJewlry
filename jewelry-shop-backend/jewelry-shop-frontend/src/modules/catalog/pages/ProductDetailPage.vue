<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { cartApi } from '@/modules/cart/api/cartApi'
import { catalogApi } from '@/modules/catalog/api/catalogApi'
import type { ProductDetail, ProductImageSummary, ProductVariantSummary } from '@/modules/catalog/types/catalog'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'
import { useAuthStore } from '@/stores/auth/useAuthStore'
import { useCartStore } from '@/stores/cart/useCartStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const uiStore = useUiStore()

const product = ref<ProductDetail | null>(null)
const variants = ref<ProductVariantSummary[]>([])
const images = ref<ProductImageSummary[]>([])
const selectedVariantId = ref<number | null>(null)
const quantity = ref(1)
const isLoading = ref(false)
const isAddingToCart = ref(false)
const errorMessage = ref('')

const activeImage = computed(() => images.value.find((image) => image.isMain) ?? images.value[0] ?? null)
const selectedVariant = computed(
  () => variants.value.find((variant) => variant.id === selectedVariantId.value) ?? variants.value[0] ?? null,
)
const displayPrice = computed(() => selectedVariant.value?.price ?? product.value?.basePrice ?? 0)

async function loadProduct() {
  const slug = String(route.params.slug ?? '')
  isLoading.value = true
  errorMessage.value = ''

  try {
    const [productResponse, variantsResponse, imagesResponse] = await Promise.all([
      catalogApi.getProductDetail(slug),
      catalogApi.getProductVariants(slug),
      catalogApi.getProductImages(slug),
    ])

    product.value = productResponse
    variants.value = variantsResponse.content
    images.value = imagesResponse.content.sort((left, right) => (left.sortOrder ?? 999) - (right.sortOrder ?? 999))
    selectedVariantId.value = variants.value[0]?.id ?? null
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải chi tiết sản phẩm'
  } finally {
    isLoading.value = false
  }
}

async function handleAddToCart() {
  if (!selectedVariant.value) {
    uiStore.pushToast('Sản phẩm này chưa có biến thể khả dụng', 'error')
    return
  }

  if (!authStore.isAuthenticated) {
    await router.push('/login')
    return
  }

  isAddingToCart.value = true

  try {
    const cart = await cartApi.addItem({
      productVariantId: selectedVariant.value.id,
      quantity: quantity.value,
    })

    cartStore.setItemCount(cart.items.reduce((total, item) => total + item.quantity, 0))
    uiStore.pushToast('Đã thêm sản phẩm vào giỏ', 'success')
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể thêm vào giỏ', 'error')
  } finally {
    isAddingToCart.value = false
  }
}

watch(
  () => route.params.slug,
  async () => {
    await loadProduct()
  },
)

onMounted(async () => {
  await loadProduct()
})
</script>

<template>
  <div class="stack-md">
    <PageHero
      eyebrow="Chi tiết sản phẩm"
      :title="product?.name ?? 'Đang tải sản phẩm...'"
      :description="product?.description ?? 'Chi tiết sản phẩm, biến thể và hình ảnh sẽ hiển thị tại đây.'"
    />

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải chi tiết sản phẩm...</p>
    </section>

    <section v-else-if="errorMessage" class="surface-card">
      <p class="error-text">{{ errorMessage }}</p>
    </section>

    <section v-else-if="product" class="detail-grid">
      <article class="surface-card">
        <div class="detail-image">
          <img v-if="activeImage" :src="activeImage.imageUrl" :alt="product.name" />
          <div v-else class="image-fallback large-fallback">Vedora</div>
        </div>
        <div v-if="images.length > 1" class="thumb-row">
          <img
            v-for="image in images"
            :key="image.id"
            :src="image.imageUrl"
            :alt="product.name"
            class="thumb-image"
          />
        </div>
      </article>

      <article class="surface-card stack-md">
        <div>
          <p class="muted-label">{{ product.categoryName }}</p>
          <h2 class="product-title detail-title">{{ product.name }}</h2>
          <p class="price-text">{{ formatCurrency(displayPrice) }}</p>
          <p class="page-copy">{{ product.description }}</p>
        </div>

        <div class="info-list">
          <p><strong>Chất liệu:</strong> {{ product.material || 'Đang cập nhật' }}</p>
          <p><strong>Giá gốc:</strong> {{ formatCurrency(product.basePrice) }}</p>
          <p><strong>Tồn kho:</strong> {{ selectedVariant?.stockQuantity ?? 0 }}</p>
        </div>

        <label class="field">
          <span>Biến thể</span>
          <select v-model="selectedVariantId">
            <option v-for="variant in variants" :key="variant.id" :value="variant.id">
              {{ variant.sku }} - {{ formatCurrency(variant.price) }} - Tồn {{ variant.stockQuantity }}
            </option>
          </select>
        </label>

        <label class="field small-field">
          <span>Số lượng</span>
          <input v-model.number="quantity" type="number" min="1" :max="selectedVariant?.stockQuantity ?? 1" />
        </label>

        <button class="primary-button" type="button" :disabled="isAddingToCart" @click="handleAddToCart()">
          {{ isAddingToCart ? 'Đang thêm...' : 'Thêm vào giỏ' }}
        </button>
      </article>
    </section>
  </div>
</template>
