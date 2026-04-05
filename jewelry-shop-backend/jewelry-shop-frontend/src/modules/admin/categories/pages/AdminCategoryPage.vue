<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { categoryAdminApi } from '@/modules/admin/categories/api/categoryAdminApi'
import type { AdminCategory } from '@/modules/admin/categories/types/category'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()

const categories = ref<AdminCategory[]>([])
const selectedId = ref<number | null>(null)
const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')

const filters = reactive({
  q: '',
  status: '',
})

const form = reactive({
  name: '',
  slug: '',
  description: '',
})

function resetForm() {
  selectedId.value = null
  form.name = ''
  form.slug = ''
  form.description = ''
}

function fillForm(category: AdminCategory) {
  selectedId.value = category.id
  form.name = category.name
  form.slug = category.slug
  form.description = category.description ?? ''
}

async function loadCategories() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await categoryAdminApi.getAll(filters.q, filters.status)
    categories.value = response.content
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải danh mục'
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
    }

    if (selectedId.value) {
      await categoryAdminApi.update(selectedId.value, payload)
      uiStore.pushToast('Đã cập nhật danh mục', 'success')
    } else {
      await categoryAdminApi.create(payload)
      uiStore.pushToast('Đã tạo danh mục', 'success')
    }

    resetForm()
    await loadCategories()
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể lưu danh mục'
  } finally {
    isSaving.value = false
  }
}

async function handleStatusChange(category: AdminCategory, status: 'ACTIVE' | 'INACTIVE') {
  try {
    await categoryAdminApi.changeStatus(category.id, { status })
    uiStore.pushToast('Đã cập nhật trạng thái', 'success')
    await loadCategories()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể đổi trạng thái', 'error')
  }
}

async function handleDelete(category: AdminCategory) {
  if (!window.confirm(`Xóa danh mục "${category.name}"?`)) {
    return
  }

  try {
    await categoryAdminApi.delete(category.id)
    if (selectedId.value === category.id) {
      resetForm()
    }
    uiStore.pushToast('Đã xóa danh mục', 'success')
    await loadCategories()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể xóa danh mục', 'error')
  }
}

onMounted(async () => {
  await loadCategories()
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Admin" title="Category Management" description="Quản lý danh mục, trạng thái và nội dung hiển thị." />

    <section class="admin-grid">
      <article class="surface-card stack-md">
        <div class="section-heading">
          <h2>{{ selectedId ? 'Chỉnh sửa danh mục' : 'Tạo danh mục mới' }}</h2>
          <button v-if="selectedId" class="ghost-button" type="button" @click="resetForm()">Tạo mới</button>
        </div>

        <form class="stack-md" @submit.prevent="handleSubmit()">
          <label class="field">
            <span>Tên danh mục</span>
            <input v-model.trim="form.name" type="text" required />
          </label>
          <label class="field">
            <span>Slug</span>
            <input v-model.trim="form.slug" type="text" placeholder="nhan" />
          </label>
          <label class="field">
            <span>Mô tả</span>
            <textarea v-model.trim="form.description" rows="5" />
          </label>
          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
          <button class="primary-button" type="submit" :disabled="isSaving">
            {{ isSaving ? 'Đang lưu...' : selectedId ? 'Cập nhật danh mục' : 'Tạo danh mục' }}
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
            <span>Trạng thái</span>
            <select v-model="filters.status">
              <option value="">Tất cả</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="INACTIVE">INACTIVE</option>
            </select>
          </label>
          <button class="primary-button align-end" type="button" @click="loadCategories()">Lọc</button>
        </div>

        <p v-if="isLoading">Đang tải danh mục...</p>
        <p v-else-if="categories.length === 0">Chưa có danh mục phù hợp.</p>

        <div v-else class="table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Tên</th>
                <th>Slug</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="category in categories" :key="category.id">
                <td>{{ category.name }}</td>
                <td>{{ category.slug }}</td>
                <td>{{ category.status }}</td>
                <td class="action-row">
                  <button class="ghost-button" type="button" @click="fillForm(category)">Sửa</button>
                  <button
                    class="ghost-button"
                    type="button"
                    @click="handleStatusChange(category, category.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')"
                  >
                    {{ category.status === 'ACTIVE' ? 'Ẩn' : 'Kích hoạt' }}
                  </button>
                  <button class="ghost-button danger-button" type="button" @click="handleDelete(category)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>
