<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { accountAdminApi } from '@/modules/admin/accounts/api/accountAdminApi'
import type { AdminAccount } from '@/modules/admin/accounts/types/account'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()

const accounts = ref<AdminAccount[]>([])
const selectedId = ref<number | null>(null)
const isLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')
const debugMessage = ref('')

const availableRoles = ref<string[]>(['ROLE_USER', 'ROLE_ADMIN'])

const filters = reactive({
  q: '',
  status: '',
})

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  address: '',
  password: '',
  status: true,
  roles: ['ROLE_USER'] as string[],
})

function resetForm() {
  selectedId.value = null
  form.fullName = ''
  form.email = ''
  form.phone = ''
  form.address = ''
  form.password = ''
  form.status = true
  form.roles = ['ROLE_USER']
  errorMessage.value = ''
}

function fillForm(account: AdminAccount) {
  selectedId.value = account.id
  form.fullName = account.fullName
  form.email = account.email
  form.phone = account.phone ?? ''
  form.address = account.address ?? ''
  form.password = ''
  form.status = account.status
  form.roles = [...account.roles]
  errorMessage.value = ''
}

function validateForm() {
  if (!form.fullName.trim()) return 'Vui lòng nhập họ và tên'
  if (!form.email.trim()) return 'Vui lòng nhập email'
  if (!selectedId.value && !form.password.trim()) return 'Vui lòng nhập mật khẩu'
  if (form.roles.length === 0) return 'Vui lòng chọn ít nhất một vai trò'
  return ''
}

function getErrorMessage(error: unknown) {
  if (error instanceof HttpError) return error.message
  if (error instanceof Error) return error.message
  return 'Có lỗi xảy ra'
}

async function loadRoles() {
  try {
    const response = await accountAdminApi.getRoles()

    if (Array.isArray(response)) {
      availableRoles.value = response
    } else if (Array.isArray((response as any)?.data)) {
      availableRoles.value = (response as any).data
    } else if (Array.isArray((response as any)?.result)) {
      availableRoles.value = (response as any).result
    } else {
      availableRoles.value = ['ROLE_USER', 'ROLE_ADMIN']
    }

    if (!availableRoles.value.length) {
      availableRoles.value = ['ROLE_USER', 'ROLE_ADMIN']
    }
  } catch (error) {
    console.error('loadRoles error:', error)
    availableRoles.value = ['ROLE_USER', 'ROLE_ADMIN']
  }
}

async function loadAccounts() {
  isLoading.value = true
  errorMessage.value = ''
  debugMessage.value = ''

  try {
    const response = await accountAdminApi.getAll({
      q: filters.q,
      status: filters.status,
      page: 0,
      size: 100,
    })

    accounts.value = response.content ?? []
    debugMessage.value = `Đã tải ${accounts.value.length} tài khoản`
  } catch (error) {
    console.error('loadAccounts error:', error)
    errorMessage.value =
      error instanceof HttpError
        ? `${error.status} - ${error.message}`
        : 'Không thể tải tài khoản'
    debugMessage.value = 'Backend đang lỗi tại /api/admin/accounts'
  } finally {
    isLoading.value = false
  }
}

async function handleSubmit() {
  const validationMessage = validateForm()
  if (validationMessage) {
    errorMessage.value = validationMessage
    return
  }

  isSaving.value = true
  errorMessage.value = ''

  try {
    const payload = {
      fullName: form.fullName.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
      address: form.address.trim(),
      password: form.password.trim() || undefined,
      status: form.status,
      roles: form.roles,
    }

    if (selectedId.value) {
      await accountAdminApi.update(selectedId.value, payload)
      uiStore.pushToast('Đã cập nhật tài khoản', 'success')
    } else {
      await accountAdminApi.create(payload)
      uiStore.pushToast('Đã tạo tài khoản', 'success')
    }

    resetForm()
    await loadAccounts()
  } catch (error) {
    errorMessage.value = getErrorMessage(error)
    console.error('handleSubmit error:', error)
  } finally {
    isSaving.value = false
  }
}

async function handleDelete(account: AdminAccount) {
  const confirmed = window.confirm(`Xóa tài khoản "${account.fullName}"?`)
  if (!confirmed) return

  try {
    await accountAdminApi.delete(account.id)

    if (selectedId.value === account.id) {
      resetForm()
    }

    uiStore.pushToast('Đã xóa tài khoản', 'success')
    await loadAccounts()
  } catch (error) {
    const message = getErrorMessage(error)
    uiStore.pushToast(message, 'error')
    console.error('handleDelete error:', error)
  }
}

function handleFilter() {
  loadAccounts()
}

function clearFilters() {
  filters.q = ''
  filters.status = ''
  loadAccounts()
}

onMounted(async () => {
  await loadRoles()
  await loadAccounts()
})
</script>

<template>
  <div class="account-page stack-md">
    <PageHero
      eyebrow="Admin"
      title="Account Management"
      description="Quản lý tài khoản người dùng, vai trò và trạng thái hoạt động."
    />

    <section class="admin-grid">
      <article class="surface-card stack-md">
        <div class="section-heading">
          <h2>{{ selectedId ? 'Chỉnh sửa tài khoản' : 'Tạo tài khoản mới' }}</h2>
          <button v-if="selectedId" class="ghost-button" type="button" @click="resetForm">
            Tạo mới
          </button>
        </div>

        <form class="stack-md" @submit.prevent="handleSubmit">
          <label class="field">
            <span>Họ và tên</span>
            <input v-model.trim="form.fullName" type="text" required />
          </label>

          <label class="field">
            <span>Email</span>
            <input v-model.trim="form.email" type="email" required />
          </label>

          <div class="two-col-grid">
            <label class="field">
              <span>Số điện thoại</span>
              <input v-model.trim="form.phone" type="text" />
            </label>

            <label class="field">
              <span>Mật khẩu</span>
              <input
                v-model.trim="form.password"
                type="password"
                :required="!selectedId"
                :placeholder="selectedId ? 'Để trống nếu không đổi mật khẩu' : 'Nhập mật khẩu'"
              />
            </label>
          </div>

          <label class="field">
            <span>Địa chỉ</span>
            <textarea v-model.trim="form.address" rows="4"></textarea>
          </label>

          <label class="field">
            <span>Vai trò</span>
            <div class="role-picker">
              <label
                v-for="role in availableRoles"
                :key="role"
                class="role-option"
              >
                <input
                  v-model="form.roles"
                  type="checkbox"
                  :value="role"
                />
                <span>{{ role }}</span>
              </label>
            </div>
            <small class="field-hint">Có thể chọn nhiều vai trò cho một user</small>
          </label>

          <label class="checkbox-field">
            <input v-model="form.status" type="checkbox" />
            <span>Đang hoạt động</span>
          </label>

          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

          <button class="primary-button" type="submit" :disabled="isSaving">
            {{ isSaving ? 'Đang lưu...' : selectedId ? 'Cập nhật tài khoản' : 'Tạo tài khoản' }}
          </button>
        </form>
      </article>

      <article class="surface-card stack-md">
        <div class="filter-grid">
          <label class="field">
            <span>Tìm kiếm</span>
            <input
              v-model.trim="filters.q"
              type="text"
              placeholder="Tên hoặc email"
              @keyup.enter="handleFilter"
            />
          </label>

          <label class="field">
            <span>Trạng thái</span>
            <select v-model="filters.status">
              <option value="">Tất cả</option>
              <option value="true">ACTIVE</option>
              <option value="false">INACTIVE</option>
            </select>
          </label>

          <button class="primary-button align-end" type="button" @click="handleFilter">
            Lọc
          </button>

          <button class="ghost-button align-end" type="button" @click="clearFilters">
            Xóa lọc
          </button>
        </div>

        <p v-if="debugMessage" class="debug-text">{{ debugMessage }}</p>
        <p v-if="isLoading">Đang tải tài khoản...</p>
        <p v-else-if="accounts.length === 0">Chưa có tài khoản phù hợp.</p>

        <div v-else class="table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="account in accounts" :key="account.id">
                <td>
                  <div class="stack-sm">
                    <strong>{{ account.fullName }}</strong>
                    <span class="muted-label">#{{ account.id }}</span>
                  </div>
                </td>
                <td>{{ account.email }}</td>
                <td>{{ account.phone || '—' }}</td>
                <td>
                  <div class="role-list">
                    <span v-for="role in account.roles" :key="role" class="role-badge">
                      {{ role }}
                    </span>
                  </div>
                </td>
                <td>
                  <span :class="account.status ? 'status-active' : 'status-inactive'">
                    {{ account.status ? 'ACTIVE' : 'INACTIVE' }}
                  </span>
                </td>
                <td class="action-row">
                  <button class="ghost-button" type="button" @click="fillForm(account)">
                    Sửa
                  </button>
                  <button class="ghost-button danger-button" type="button" @click="handleDelete(account)">
                    Xóa
                  </button>
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
.account-page {
  display: grid;
  gap: 1rem;
}

.stack-md {
  display: grid;
  gap: 1rem;
}

.stack-sm {
  display: grid;
  gap: 0.25rem;
}

.admin-grid {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: 1.25rem;
}

.surface-card {
  background: #f8f2eb;
  border: 1px solid #eadccf;
  border-radius: 24px;
  padding: 1.25rem;
  box-shadow: 0 10px 30px rgba(60, 40, 20, 0.06);
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.section-heading h2 {
  margin: 0;
  font-size: 1.25rem;
}

.field {
  display: grid;
  gap: 0.45rem;
}

.field span {
  font-size: 0.95rem;
  color: #5b4636;
}

.field input,
.field textarea,
.field select {
  width: 100%;
  border: 1px solid #d9c7b7;
  background: #fff;
  border-radius: 16px;
  padding: 0.9rem 1rem;
  font: inherit;
  outline: none;
}

.field textarea {
  resize: vertical;
  min-height: 110px;
}

.two-col-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.filter-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr auto auto;
  gap: 1rem;
  align-items: end;
}

.checkbox-field {
  display: flex;
  align-items: center;
  gap: 0.55rem;
  color: #4f4034;
}

.primary-button,
.ghost-button {
  border-radius: 999px;
  padding: 0.9rem 1.25rem;
  font: inherit;
  cursor: pointer;
  transition: 0.2s ease;
}

.primary-button {
  border: 0;
  background: linear-gradient(90deg, #b68a4b 0%, #8f6729 100%);
  color: #fff;
  font-weight: 600;
}

.primary-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.ghost-button {
  border: 1px solid #d9c7b7;
  background: #fff;
  color: #5a4431;
}

.danger-button {
  color: #b63a2f;
}

.align-end {
  align-self: end;
}

.error-text {
  margin: 0;
  color: #c45243;
}

.debug-text {
  margin: 0;
  color: #8c6b45;
  font-size: 0.92rem;
}

.table-wrap {
  overflow: auto;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th,
.admin-table td {
  text-align: left;
  padding: 0.9rem 0.75rem;
  border-bottom: 1px solid #eadccf;
  vertical-align: top;
}

.admin-table th {
  color: #7a624f;
  font-size: 0.9rem;
  font-weight: 600;
}

.role-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.3rem 0.6rem;
  border-radius: 999px;
  background: #efe2d3;
  color: #6c523c;
  font-size: 0.8rem;
  font-weight: 600;
}

.role-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.role-option {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.65rem 0.9rem;
  border: 1px solid #d9c7b7;
  border-radius: 999px;
  background: #fff;
  color: #5b4636;
  cursor: pointer;
}

.role-option input {
  width: auto;
  margin: 0;
}

.field-hint {
  color: #8e7866;
  font-size: 0.85rem;
}

.status-active {
  color: #1d7a43;
  font-weight: 600;
}

.status-inactive {
  color: #a05536;
  font-weight: 600;
}

.muted-label {
  color: #8e7866;
  font-size: 0.85rem;
}

.action-row {
  display: flex;
  gap: 0.5rem;
}

@media (max-width: 1100px) {
  .admin-grid {
    grid-template-columns: 1fr;
  }

  .filter-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 700px) {
  .two-col-grid,
  .filter-grid {
    grid-template-columns: 1fr;
  }

  .surface-card {
    padding: 1rem;
  }
}
</style>
