<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { orderAdminApi } from '@/modules/admin/orders/api/orderAdminApi'
import type { AdminOrderDetail, AdminOrderSummary } from '@/modules/admin/orders/types/order'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'
import { useUiStore } from '@/stores/ui/useUiStore'

const uiStore = useUiStore()

const orders = ref<AdminOrderSummary[]>([])
const selectedOrder = ref<AdminOrderDetail | null>(null)
const isLoading = ref(false)
const isDetailLoading = ref(false)
const isSaving = ref(false)
const errorMessage = ref('')

const filters = reactive({
  q: '',
  orderStatus: '',
  paymentStatus: '',
})

const updateForm = reactive({
  orderStatus: '',
  paymentStatus: '',
})

const orderStatusOptions = ['PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED'] as const
const paymentStatusOptions = ['UNPAID', 'PAID'] as const

async function loadOrders() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await orderAdminApi.getAll(filters.orderStatus, filters.paymentStatus, filters.q)
    orders.value = response.content
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải đơn hàng'
  } finally {
    isLoading.value = false
  }
}

async function selectOrder(orderId: number) {
  isDetailLoading.value = true

  try {
    const response = await orderAdminApi.getById(orderId)
    selectedOrder.value = response
    updateForm.orderStatus = response.orderStatus
    updateForm.paymentStatus = response.paymentStatus
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể tải chi tiết đơn', 'error')
  } finally {
    isDetailLoading.value = false
  }
}

async function handleUpdateStatus() {
  if (!selectedOrder.value) {
    return
  }

  isSaving.value = true

  try {
    const response = await orderAdminApi.updateStatus(selectedOrder.value.id, {
      orderStatus: updateForm.orderStatus as AdminOrderDetail['orderStatus'],
      paymentStatus: updateForm.paymentStatus as AdminOrderDetail['paymentStatus'],
    })

    selectedOrder.value = response
    uiStore.pushToast('Đã cập nhật trạng thái đơn hàng', 'success')
    await loadOrders()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể cập nhật trạng thái', 'error')
  } finally {
    isSaving.value = false
  }
}

onMounted(async () => {
  await loadOrders()
})
function getStatusClass(status: string) {
  const statusMap: Record<string, string> = {
    // Order Status
    PENDING: 'badge-warning',
    CONFIRMED: 'badge-info',
    SHIPPING: 'badge-primary',
    DELIVERED: 'badge-success',
    CANCELLED: 'badge-danger',
    // Payment Status
    PAID: 'badge-success',
    UNPAID: 'badge-danger',
  }
  return statusMap[status] || 'badge-secondary'
}
</script>
<template>
  <div class="stack-md admin-container">
    <PageHero
      eyebrow="Admin Dashboard"
      title="Quản lý đơn hàng"
      description="Theo dõi lịch sử giao dịch và cập nhật tiến độ xử lý đơn hàng."
    />

    <section class="admin-grid">
      <article class="surface-card stack-md shadow-sm">
        <div class="section-header flex-between">
          <h2 class="text-lg font-bold">Danh sách đơn</h2>
          <button class="ghost-button icon-only" @click="loadOrders" :disabled="isLoading">
            <span :class="{ 'spin': isLoading }">🔄</span>
          </button>
        </div>

        <div class="filter-panel bg-light p-md rounded-md">
          <div class="filter-grid">
            <label class="field">
              <span class="label-text">Tìm kiếm</span>
              <input v-model.trim="filters.q" type="text" placeholder="Mã đơn, tên..." @keyup.enter="loadOrders" />
            </label>
            <div class="two-col-grid">
              <label class="field">
                <span class="label-text">Đơn hàng</span>
                <select v-model="filters.orderStatus">
                  <option value="">Tất cả</option>
                  <option v-for="status in orderStatusOptions" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
              <label class="field">
                <span class="label-text">Thanh toán</span>
                <select v-model="filters.paymentStatus">
                  <option value="">Tất cả</option>
                  <option v-for="status in paymentStatusOptions" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
            </div>
            <button class="primary-button w-full" type="button" @click="loadOrders()">
              {{ isLoading ? 'Đang lọc...' : 'Áp dụng bộ lọc' }}
            </button>
          </div>
        </div>

        <div class="table-container shadow-inner">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Mã đơn</th>
                <th>Người nhận</th>
                <th>Trạng thái</th>
                <th>Giá trị</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id" :class="{ 'active-row': selectedOrder?.id === order.id }">
                <td class="font-mono font-bold text-primary">{{ order.orderCode }}</td>
                <td>
                  <div class="stack-xs">
                    <span class="font-medium">{{ order.receiverName }}</span>
                    <small class="text-muted">{{ order.paymentStatus }}</small>
                  </div>
                </td>
                <td>
                  <span class="badge" :class="getStatusClass(order.orderStatus)">
                    {{ order.orderStatus }}
                  </span>
                </td>
                <td class="font-bold">{{ formatCurrency(order.finalAmount) }}</td>
                <td class="text-right">
                  <button class="action-btn" type="button" @click="selectOrder(order.id)">
                    →
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <div v-if="orders.length === 0 && !isLoading" class="empty-state p-xl">
            <p>Không có dữ liệu đơn hàng.</p>
          </div>
        </div>
      </article>

      <article class="surface-card stack-lg shadow-sm sticky-top">
        <div class="section-heading border-bottom pb-md">
          <h2 class="text-lg font-bold">Thông tin chi tiết</h2>
        </div>

        <div v-if="isDetailLoading" class="loading-overlay">
          <p>Đang tải dữ liệu...</p>
        </div>

        <div v-else-if="!selectedOrder" class="empty-state">
          <div class="stack-sm align-center text-muted p-xl">
            <span>📋</span>
            <p>Chọn đơn hàng bên trái để thao tác</p>
          </div>
        </div>

        <template v-else>
          <div class="info-group grid-2-cols">
            <div class="info-item">
              <label>Khách hàng</label>
              <p class="font-medium">{{ selectedOrder.userEmail }}</p>
            </div>
            <div class="info-item text-right">
              <label>Tổng thanh toán</label>
              <p class="text-xl font-bold text-primary">{{ formatCurrency(selectedOrder.finalAmount) }}</p>
            </div>
          </div>

          <div class="info-card stack-sm">
            <div class="info-row">
              <span class="label">Người nhận:</span>
              <span>{{ selectedOrder.receiverName }} - {{ selectedOrder.receiverPhone }}</span>
            </div>
            <div class="info-row">
              <span class="label">Địa chỉ:</span>
              <span>{{ selectedOrder.receiverAddress }}</span>
            </div>
            <div class="info-row" v-if="selectedOrder.note">
              <span class="label">Ghi chú:</span>
              <span class="text-italic">"{{ selectedOrder.note }}"</span>
            </div>
          </div>

          <div class="items-list shadow-inner rounded-md">
            <table class="compact-table">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th class="text-center">SL</th>
                  <th class="text-right">Tổng</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.items" :key="item.id">
                  <td>
                    <div class="stack-xs">
                      <span class="text-sm font-medium">{{ item.productName }}</span>
                      <small class="text-muted">{{ item.variantInfo || 'Default' }}</small>
                    </div>
                  </td>
                  <td class="text-center">x{{ item.quantity }}</td>
                  <td class="text-right font-medium">{{ formatCurrency(item.subtotal) }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <form class="update-box stack-md p-md rounded-md bg-light border" @submit.prevent="handleUpdateStatus()">
            <h3 class="text-sm font-bold uppercase tracking-wider">Cập nhật tiến độ</h3>
            <div class="two-col-grid">
              <label class="field">
                <span class="label-text small">Trạng thái đơn</span>
                <select v-model="updateForm.orderStatus" class="select-sm">
                  <option v-for="status in orderStatusOptions" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
              <label class="field">
                <span class="label-text small">Thanh toán</span>
                <select v-model="updateForm.paymentStatus" class="select-sm">
                  <option v-for="status in paymentStatusOptions" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
            </div>
            <button class="primary-button w-full shadow-button" type="submit" :disabled="isSaving">
              {{ isSaving ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </form>
        </template>
      </article>
    </section>
  </div>
</template>
<style scoped>
.admin-container {
  padding-bottom: 3rem;
}

.admin-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 1.5rem;
  align-items: start;
}

/* Badge & Status Styles */
.badge {
  display: inline-flex;
  padding: 0.25rem 0.6rem;
  border-radius: 99px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}
.badge-warning { background: #fff7ed; color: #c2410c; border: 1px solid #fdba74; }
.badge-success { background: #f0fdf4; color: #15803d; border: 1px solid #86efac; }
.badge-danger { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }
.badge-info { background: #f0f9ff; color: #0369a1; border: 1px solid #7dd3fc; }
.badge-primary { background: #eef2ff; color: #4338ca; border: 1px solid #a5b4fc; }

/* Table Improvements */
.active-row {
  background-color: var(--primary-light, #f0f7ff);
}

.action-btn {
  background: none;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  transition: all 0.2s;
}
.action-btn:hover {
  background: #111;
  color: white;
}

/* Detail Box */
.sticky-top {
  position: sticky;
  top: 1rem;
}

.info-card {
  background: #f8fafc;
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
}

.info-row {
  display: flex;
  gap: 0.5rem;
}
.info-row .label {
  color: #64748b;
  min-width: 80px;
}

.update-box {
  background-color: #f1f5f9;
}

.label-text.small {
  font-size: 0.7rem;
  font-weight: 700;
  margin-bottom: 4px;
}

@media (max-width: 1024px) {
  .admin-grid {
    grid-template-columns: 1fr;
  }
}

.spin {
  animation: rotate 1s linear infinite;
  display: inline-block;
}
@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
</style>
