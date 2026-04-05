<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { imageAdminApi } from '@/modules/admin/images/api/imageAdminApi'
import type { AdminImage } from '@/modules/admin/images/types/image'
import { productAdminApi } from '@/modules/admin/products/api/productAdminApi'
import type { AdminProduct } from '@/modules/admin/products/types/product'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()

const images = ref<AdminImage[]>([])
const products = ref<AdminProduct[]>([])
const selectedId = ref<number | null>(null)
const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')

const filters = reactive({
  productId: '',
  isMain: '',
})

const form = reactive({
  productId: '',
  imageUrl: '',
  isMain: false,
  sortOrder: 0,
})

function resetForm() {
  selectedId.value = null
  form.productId = ''
  form.imageUrl = ''
  form.isMain = false
  form.sortOrder = 0
}

function fillForm(image: AdminImage) {
  selectedId.value = image.id
  form.productId = String(image.productId)
  form.imageUrl = image.imageUrl
  form.isMain = image.isMain
  form.sortOrder = image.sortOrder ?? 0
}

async function loadSupportingData() {
  const response = await productAdminApi.getAll('', '', 'true')
  products.value = response.content
}

async function loadImages() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await imageAdminApi.getAll(filters.productId, filters.isMain)
    images.value = response.content
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải hình ảnh'
  } finally {
    isLoading.value = false
  }
}

async function handleSubmit() {
  isSaving.value = true
  errorMessage.value = ''

  try {
    if (selectedId.value) {
      await imageAdminApi.update(selectedId.value, {
        imageUrl: form.imageUrl,
        isMain: form.isMain,
        sortOrder: Number(form.sortOrder),
      })
      uiStore.pushToast('Đã cập nhật hình ảnh', 'success')
    } else {
      await imageAdminApi.create({
        productId: Number(form.productId),
        imageUrl: form.imageUrl,
        isMain: form.isMain,
        sortOrder: Number(form.sortOrder),
      })
      uiStore.pushToast('Đã tạo hình ảnh', 'success')
    }

    resetForm()
    await loadImages()
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể lưu hình ảnh'
  } finally {
    isSaving.value = false
  }
}

async function handleDelete(image: AdminImage) {
  if (!window.confirm(`Xóa ảnh của "${image.productName}"?`)) {
    return
  }

  try {
    await imageAdminApi.delete(image.id)
    if (selectedId.value === image.id) {
      resetForm()
    }
    uiStore.pushToast('Đã xóa hình ảnh', 'success')
    await loadImages()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể xóa hình ảnh', 'error')
  }
}

onMounted(async () => {
  await Promise.all([loadSupportingData(), loadImages()])
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Admin" title="Image Management" description="Quản lý URL hình ảnh, ảnh chính và thứ tự hiển thị của sản phẩm." />

    <section class="admin-grid">
      <article class="surface-card stack-md">
        <div class="section-heading">
          <h2>{{ selectedId ? 'Chỉnh sửa hình ảnh' : 'Tạo hình ảnh mới' }}</h2>
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
            <span>Image URL</span>
            <input v-model.trim="form.imageUrl" type="url" required />
          </label>
          <label class="field">
            <span>Thứ tự</span>
            <input v-model.number="form.sortOrder" type="number" min="0" />
          </label>
          <label class="field checkbox-field">
            <input v-model="form.isMain" type="checkbox" />
            <span>Ảnh chính</span>
          </label>
          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
          <button class="primary-button" type="submit" :disabled="isSaving">
            {{ isSaving ? 'Đang lưu...' : selectedId ? 'Cập nhật hình ảnh' : 'Tạo hình ảnh' }}
          </button>
        </form>
      </article>

      <article class="surface-card stack-md">
        <div class="filter-grid compact-grid">
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
            <span>Ảnh chính</span>
            <select v-model="filters.isMain">
              <option value="">Tất cả</option>
              <option value="true">Main</option>
              <option value="false">Gallery</option>
            </select>
          </label>
          <button class="primary-button align-end" type="button" @click="loadImages()">Lọc</button>
        </div>

        <p v-if="isLoading">Đang tải hình ảnh...</p>
        <p v-else-if="images.length === 0">Chưa có hình ảnh phù hợp.</p>

        <div v-else class="table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Preview</th>
                <th>Sản phẩm</th>
                <th>Loại</th>
                <th>Thứ tự</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="image in images" :key="image.id">
                <td><img :src="image.imageUrl" :alt="image.productName" class="admin-thumb" /></td>
                <td>{{ image.productName }}</td>
                <td>{{ image.isMain ? 'MAIN' : 'GALLERY' }}</td>
                <td>{{ image.sortOrder ?? 0 }}</td>
                <td class="action-row">
                  <button class="ghost-button" type="button" @click="fillForm(image)">Sửa</button>
                  <button class="ghost-button danger-button" type="button" @click="handleDelete(image)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>
