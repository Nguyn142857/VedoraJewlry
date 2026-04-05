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
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Admin" title="Order Management" description="Theo dõi đơn hàng, xem chi tiết và cập nhật trạng thái xử lý." />

    <section class="admin-grid">
      <article class="surface-card stack-md">
        <div class="filter-grid compact-grid">
          <label class="field">
            <span>Tìm kiếm</span>
            <input v-model.trim="filters.q" type="text" placeholder="Mã đơn hoặc tên người nhận" />
          </label>
          <label class="field">
            <span>Trạng thái đơn</span>
            <select v-model="filters.orderStatus">
              <option value="">Tất cả</option>
              <option v-for="status in orderStatusOptions" :key="status" :value="status">{{ status }}</option>
            </select>
          </label>
          <label class="field">
            <span>Thanh toán</span>
            <select v-model="filters.paymentStatus">
              <option value="">Tất cả</option>
              <option v-for="status in paymentStatusOptions" :key="status" :value="status">{{ status }}</option>
            </select>
          </label>
          <button class="primary-button align-end" type="button" @click="loadOrders()">Lọc</button>
        </div>

        <p v-if="isLoading">Đang tải đơn hàng...</p>
        <p v-else-if="orders.length === 0">Chưa có đơn hàng phù hợp.</p>

        <div v-else class="table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Mã đơn</th>
                <th>Người nhận</th>
                <th>Trạng thái</th>
                <th>Thanh toán</th>
                <th>Giá trị</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id">
                <td>{{ order.orderCode }}</td>
                <td>{{ order.receiverName }}</td>
                <td>{{ order.orderStatus }}</td>
                <td>{{ order.paymentStatus }}</td>
                <td>{{ formatCurrency(order.finalAmount) }}</td>
                <td class="action-row">
                  <button class="ghost-button" type="button" @click="selectOrder(order.id)">Xem chi tiết</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="surface-card stack-md">
        <div class="section-heading">
          <h2>Chi tiết đơn hàng</h2>
        </div>

        <p v-if="isDetailLoading">Đang tải chi tiết đơn...</p>
        <p v-else-if="!selectedOrder">Chọn một đơn hàng để xem chi tiết và cập nhật trạng thái.</p>

        <template v-else>
          <div class="detail-meta">
            <p><strong>Mã đơn:</strong> {{ selectedOrder.orderCode }}</p>
            <p><strong>Email khách:</strong> {{ selectedOrder.userEmail }}</p>
            <p><strong>Người nhận:</strong> {{ selectedOrder.receiverName }}</p>
            <p><strong>Điện thoại:</strong> {{ selectedOrder.receiverPhone }}</p>
            <p><strong>Địa chỉ:</strong> {{ selectedOrder.receiverAddress }}</p>
            <p><strong>Ghi chú:</strong> {{ selectedOrder.note || 'Không có' }}</p>
            <p><strong>Tổng cuối:</strong> {{ formatCurrency(selectedOrder.finalAmount) }}</p>
          </div>

          <div class="table-wrap">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Biến thể</th>
                  <th>SL</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.items" :key="item.id">
                  <td>{{ item.productName }}</td>
                  <td>{{ item.variantInfo || 'Mặc định' }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ formatCurrency(item.unitPrice) }}</td>
                  <td>{{ formatCurrency(item.subtotal) }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <form class="stack-md" @submit.prevent="handleUpdateStatus()">
            <div class="compact-grid two-col-grid">
              <label class="field">
                <span>Trạng thái đơn</span>
                <select v-model="updateForm.orderStatus">
                  <option v-for="status in orderStatusOptions" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
              <label class="field">
                <span>Trạng thái thanh toán</span>
                <select v-model="updateForm.paymentStatus">
                  <option v-for="status in paymentStatusOptions" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
            </div>
            <button class="primary-button" type="submit" :disabled="isSaving">
              {{ isSaving ? 'Đang lưu...' : 'Cập nhật trạng thái' }}
            </button>
          </form>
        </template>
      </article>
    </section>

    <section v-if="errorMessage" class="surface-card">
      <p class="error-text">{{ errorMessage }}</p>
    </section>
  </div>
</template>
