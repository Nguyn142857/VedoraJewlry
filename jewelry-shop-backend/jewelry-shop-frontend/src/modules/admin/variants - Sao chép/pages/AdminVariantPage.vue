<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { productAdminApi } from '@/modules/admin/products/api/productAdminApi'
import type { AdminProduct } from '@/modules/admin/products/types/product'
import { variantAdminApi } from '@/modules/admin/variants/api/variantAdminApi'
import type { AdminVariant } from '@/modules/admin/variants/types/variant'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'
import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()

const variants = ref<AdminVariant[]>([])
const products = ref<AdminProduct[]>([])
const selectedId = ref<number | null>(null)
const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')

const filters = reactive({
  q: '',
  productId: '',
  status: '',
})

const form = reactive({
  productId: '',
  sku: '',
  size: '',
  color: '',
  gemstone: '',
  price: 0,
  stockQuantity: 0,
  status: true,
})

function resetForm() {
  selectedId.value = null
  form.productId = ''
  form.sku = ''
  form.size = ''
  form.color = ''
  form.gemstone = ''
  form.price = 0
  form.stockQuantity = 0
  form.status = true
}

function fillForm(variant: AdminVariant) {
  selectedId.value = variant.id
  form.productId = String(variant.productId)
  form.sku = variant.sku
  form.size = variant.size ?? ''
  form.color = variant.color ?? ''
  form.gemstone = variant.gemstone ?? ''
  form.price = variant.price
  form.stockQuantity = variant.stockQuantity
  form.status = variant.status
}

async function loadSupportingData() {
  const response = await productAdminApi.getAll('', '', 'true')
  products.value = response.content
}

async function loadVariants() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await variantAdminApi.getAll(filters.q, filters.productId, filters.status)
    variants.value = response.content
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải biến thể'
  } finally {
    isLoading.value = false
  }
}

async function handleSubmit() {
  isSaving.value = true
  errorMessage.value = ''

  try {
    if (selectedId.value) {
      await variantAdminApi.update(selectedId.value, {
        sku: form.sku,
        size: form.size,
        color: form.color,
        gemstone: form.gemstone,
        price: Number(form.price),
        stockQuantity: Number(form.stockQuantity),
        status: form.status,
      })
      uiStore.pushToast('Đã cập nhật biến thể', 'success')
    } else {
      await variantAdminApi.create({
        productId: Number(form.productId),
        sku: form.sku,
        size: form.size,
        color: form.color,
        gemstone: form.gemstone,
        price: Number(form.price),
        stockQuantity: Number(form.stockQuantity),
        status: form.status,
      })
      uiStore.pushToast('Đã tạo biến thể', 'success')
    }

    resetForm()
    await loadVariants()
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể lưu biến thể'
  } finally {
    isSaving.value = false
  }
}

async function toggleStatus(variant: AdminVariant) {
  try {
    await variantAdminApi.changeStatus(variant.id, { status: !variant.status })
    uiStore.pushToast('Đã cập nhật trạng thái biến thể', 'success')
    await loadVariants()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể đổi trạng thái', 'error')
  }
}

async function handleDelete(variant: AdminVariant) {
  if (!window.confirm(`Xóa biến thể "${variant.sku}"?`)) {
    return
  }

  try {
    await variantAdminApi.delete(variant.id)
    if (selectedId.value === variant.id) {
      resetForm()
    }
    uiStore.pushToast('Đã xóa biến thể', 'success')
    await loadVariants()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể xóa biến thể', 'error')
  }
}

onMounted(async () => {
  await Promise.all([loadSupportingData(), loadVariants()])
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Admin" title="Variant Management" description="Quản lý SKU, tồn kho, giá bán và trạng thái của từng biến thể." />

    <section class="admin-grid">
      <article class="surface-card stack-md">
        <div class="section-heading">
          <h2>{{ selectedId ? 'Chỉnh sửa biến thể' : 'Tạo biến thể mới' }}</h2>
          <button v-if="selectedId" class="ghost-button" type="button" @click="resetForm()">Tạo mới</button>
        </div>

        <form class="stack-md" @submit.prevent="handleSubmit()">
          <label class="field">
            <span>Sản phẩm</span>
            <select v-model="form.productId" required :disabled="Boolean(selectedId)">
              <option value="">Chọn sản phẩm</option>
              <option v-for="product in products" :key="product.id" :value="String(product.id)">
                {{ product.name }}
              </option>
            </select>
          </label>
          <label class="field">
            <span>SKU</span>
            <input v-model.trim="form.sku" type="text" required />
          </label>
          <div class="compact-grid two-col-grid">
            <label class="field">
              <span>Size</span>
              <input v-model.trim="form.size" type="text" />
            </label>
            <label class="field">
              <span>Màu sắc</span>
              <input v-model.trim="form.color" type="text" />
            </label>
          </div>
          <label class="field">
            <span>Đá</span>
            <input v-model.trim="form.gemstone" type="text" />
          </label>
          <div class="compact-grid two-col-grid">
            <label class="field">
              <span>Giá bán</span>
              <input v-model.number="form.price" type="number" min="1" required />
            </label>
            <label class="field">
              <span>Tồn kho</span>
              <input v-model.number="form.stockQuantity" type="number" min="0" required />
            </label>
          </div>
          <label class="field checkbox-field">
            <input v-model="form.status" type="checkbox" />
            <span>Đang kinh doanh</span>
          </label>
          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
          <button class="primary-button" type="submit" :disabled="isSaving">
            {{ isSaving ? 'Đang lưu...' : selectedId ? 'Cập nhật biến thể' : 'Tạo biến thể' }}
          </button>
        </form>
      </article>

      <article class="surface-card stack-md">
        <div class="filter-grid compact-grid">
          <label class="field">
            <span>Tìm kiếm</span>
            <input v-model.trim="filters.q" type="text" placeholder="SKU hoặc màu/size" />
          </label>
          <label class="field">
            <span>Sản phẩm</span>
            <select v-model="filters.productId">
              <option value="">Tất cả</option>
              <option v-for="product in products" :key="product.id" :value="String(product.id)">
                {{ product.name }}
              </option>
            </select>
          </label>
          <label class="field">
            <span>Trạng thái</span>
            <select v-model="filters.status">
              <option value="">Tất cả</option>
              <option value="true">ACTIVE</option>
              <option value="false">INACTIVE</option>
            </select>
          </label>
          <button class="primary-button align-end" type="button" @click="loadVariants()">Lọc</button>
        </div>

        <p v-if="isLoading">Đang tải biến thể...</p>
        <p v-else-if="variants.length === 0">Chưa có biến thể phù hợp.</p>

        <div v-else class="table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>SKU</th>
                <th>Sản phẩm</th>
                <th>Thông tin</th>
                <th>Giá</th>
                <th>Tồn</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="variant in variants" :key="variant.id">
                <td>{{ variant.sku }}</td>
                <td>{{ variant.productName }}</td>
                <td>{{ [variant.size, variant.color, variant.gemstone].filter(Boolean).join(' / ') || 'Mặc định' }}</td>
                <td>{{ formatCurrency(variant.price) }}</td>
                <td>{{ variant.stockQuantity }}</td>
                <td>{{ variant.status ? 'ACTIVE' : 'INACTIVE' }}</td>
                <td class="action-row">
                  <button class="ghost-button" type="button" @click="fillForm(variant)">Sửa</button>
                  <button class="ghost-button" type="button" @click="toggleStatus(variant)">
                    {{ variant.status ? 'Ẩn' : 'Kích hoạt' }}
                  </button>
                  <button class="ghost-button danger-button" type="button" @click="handleDelete(variant)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>
