<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { categoryAdminApi } from '@/modules/admin/categories/api/categoryAdminApi'
import type { AdminCategory } from '@/modules/admin/categories/types/category'
import { productAdminApi } from '@/modules/admin/products/api/productAdminApi'
import type { AdminProduct } from '@/modules/admin/products/types/product'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'
import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()

const products = ref<AdminProduct[]>([])
const categories = ref<AdminCategory[]>([])
const selectedId = ref<number | null>(null)
const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')

const filters = reactive({
  q: '',
  categoryId: '',
  status: '',
})

const form = reactive({
  name: '',
  slug: '',
  description: '',
  material: '',
  basePrice: 0,
  thumbnail: '',
  status: true,
  categoryId: '',
})

function resetForm() {
  selectedId.value = null
  form.name = ''
  form.slug = ''
  form.description = ''
  form.material = ''
  form.basePrice = 0
  form.thumbnail = ''
  form.status = true
  form.categoryId = ''
}

function fillForm(product: AdminProduct) {
  selectedId.value = product.id
  form.name = product.name
  form.slug = product.slug
  form.description = product.description ?? ''
  form.material = product.material ?? ''
  form.basePrice = product.basePrice
  form.thumbnail = product.thumbnail ?? ''
  form.status = product.status
  form.categoryId = String(product.categoryId)
}

async function loadSupportingData() {
  const response = await categoryAdminApi.getAll('', 'ACTIVE')
  categories.value = response.content
}

async function loadProducts() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await productAdminApi.getAll(filters.q, filters.categoryId, filters.status)
    products.value = response.content
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải sản phẩm'
  } finally {
    isLoading.value = false
  }
}

async function handleSubmit() {
  isSaving.value = true
  errorMessage.value = ''

  try {
    const payload = {
      name: form.name,
      slug: form.slug,
      description: form.description,
      material: form.material,
      basePrice: Number(form.basePrice),
      thumbnail: form.thumbnail,
      status: form.status,
      categoryId: Number(form.categoryId),
    }

    if (selectedId.value) {
      await productAdminApi.update(selectedId.value, payload)
      uiStore.pushToast('Đã cập nhật sản phẩm', 'success')
    } else {
      await productAdminApi.create(payload)
      uiStore.pushToast('Đã tạo sản phẩm', 'success')
    }

    resetForm()
    await loadProducts()
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể lưu sản phẩm'
  } finally {
    isSaving.value = false
  }
}

async function handleDelete(product: AdminProduct) {
  if (!window.confirm(`Xóa sản phẩm "${product.name}"?`)) {
    return
  }

  try {
    await productAdminApi.delete(product.id)
    if (selectedId.value === product.id) {
      resetForm()
    }
    uiStore.pushToast('Đã xóa sản phẩm', 'success')
    await loadProducts()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể xóa sản phẩm', 'error')
  }
}

onMounted(async () => {
  await Promise.all([loadSupportingData(), loadProducts()])
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Admin" title="Product Management" description="Quản lý sản phẩm, danh mục, giá và trạng thái kinh doanh." />

    <section class="admin-grid">
      <article class="surface-card stack-md">
        <div class="section-heading">
          <h2>{{ selectedId ? 'Chỉnh sửa sản phẩm' : 'Tạo sản phẩm mới' }}</h2>
          <button v-if="selectedId" class="ghost-button" type="button" @click="resetForm()">Tạo mới</button>
        </div>

        <form class="stack-md" @submit.prevent="handleSubmit()">
          <label class="field">
            <span>Tên sản phẩm</span>
            <input v-model.trim="form.name" type="text" required />
          </label>
          <label class="field">
            <span>Slug</span>
            <input v-model.trim="form.slug" type="text" />
          </label>
          <label class="field">
            <span>Mô tả</span>
            <textarea v-model.trim="form.description" rows="4" />
          </label>
          <label class="field">
            <span>Chất liệu</span>
            <input v-model.trim="form.material" type="text" />
          </label>
          <div class="compact-grid two-col-grid">
            <label class="field">
              <span>Giá cơ bản</span>
              <input v-model.number="form.basePrice" type="number" min="1" required />
            </label>
            <label class="field">
              <span>Danh mục</span>
              <select v-model="form.categoryId" required>
                <option value="">Chọn danh mục</option>
                <option v-for="category in categories" :key="category.id" :value="String(category.id)">
                  {{ category.name }}
                </option>
              </select>
            </label>
          </div>
          <label class="field">
            <span>Thumbnail URL</span>
            <input v-model.trim="form.thumbnail" type="url" />
          </label>

          <div v-if="form.thumbnail" class="thumbnail-preview">
            <img :src="form.thumbnail" alt="Preview thumbnail" class="thumbnail-preview-image"/>
          </div>
          <label class="field checkbox-field">
            <input v-model="form.status" type="checkbox" />
            <span>Đang kinh doanh</span>
          </label>
          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
          <button class="primary-button" type="submit" :disabled="isSaving">
            {{ isSaving ? 'Đang lưu...' : selectedId ? 'Cập nhật sản phẩm' : 'Tạo sản phẩm' }}
          </button>
        </form>
      </article>

      <article class="surface-card stack-md">
        <div class="filter-grid compact-grid">
          <label class="field">
            <span>Tìm kiếm</span>
            <input v-model.trim="filters.q" type="text" placeholder="Tên hoặc slug" />
          </label>
          <label class="field">
            <span>Danh mục</span>
            <select v-model="filters.categoryId">
              <option value="">Tất cả</option>
              <option v-for="category in categories" :key="category.id" :value="String(category.id)">
                {{ category.name }}
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
          <button class="primary-button align-end" type="button" @click="loadProducts()">Lọc</button>
        </div>

        <p v-if="isLoading">Đang tải sản phẩm...</p>
        <p v-else-if="products.length === 0">Chưa có sản phẩm phù hợp.</p>

        <div v-else class="table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Ảnh</th>
                <th>Sản phẩm</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
             <tr v-for="product in products" :key="product.id">
               <td>
                 <RouterLink :to="`/products/${product.slug}`" class="product-image-link">
                   <div class="product-thumb">
                     <img
                       v-if="product.thumbnail"
                       :src="product.thumbnail"
                       :alt="product.name"
                       class="product-thumb-image"
                     />
                     <div v-else class="product-thumb-fallback">No image</div>
                   </div>
                 </RouterLink>
               </td>

               <td>
                 <RouterLink :to="`/products/${product.slug}`" class="product-link">
                   <div class="stack-sm">
                     <strong>{{ product.name }}</strong>
                     <span class="muted-label muted-inline">{{ product.slug }}</span>
                   </div>
                 </RouterLink>
               </td>

               <td>{{ product.categoryName }}</td>
               <td>{{ formatCurrency(product.basePrice) }}</td>
               <td>{{ product.status ? 'ACTIVE' : 'INACTIVE' }}</td>
               <td class="action-row">
                 <button class="ghost-button" type="button" @click="fillForm(product)">Sửa</button>
                 <button class="ghost-button danger-button" type="button" @click="handleDelete(product)">Xóa</button>
               </td>
             </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>

<style scoped>
.product-thumb {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  overflow: hidden;
  background: #f3f3f3;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-thumb-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.product-thumb-fallback {
  font-size: 0.7rem;
  color: #888;
  text-align: center;
  padding: 0.5rem;
}

.thumbnail-preview {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  overflow: hidden;
  background: #f3f3f3;
  border: 1px solid #e5e5e5;
}

.thumbnail-preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
</style>
