<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'

import { orderApi } from '@/modules/orders/api/orderApi'
import type { UserOrderSummary } from '@/modules/orders/types/order'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'

const orders = ref<UserOrderSummary[]>([])
const isLoading = ref(false)
const errorMessage = ref('')

async function loadOrders() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await orderApi.getMyOrders()
    orders.value = response.content
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải đơn hàng'
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await loadOrders()
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Đơn hàng" title="Đơn hàng của tôi" description="Theo dõi trạng thái và xem lại chi tiết từng đơn đã đặt." />

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải đơn hàng...</p>
    </section>

    <section v-else-if="errorMessage" class="surface-card">
      <p class="error-text">{{ errorMessage }}</p>
    </section>

    <section v-else-if="orders.length === 0" class="surface-card">
      <p>Bạn chưa có đơn hàng nào.</p>
    </section>

    <section v-else class="surface-card">
      <div class="table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Ngày tạo</th>
              <th>Trạng thái</th>
              <th>Thanh toán</th>
              <th>Tổng tiền</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>{{ order.orderCode }}</td>
              <td>{{ new Date(order.createdAt).toLocaleString('vi-VN') }}</td>
              <td>{{ order.orderStatus }}</td>
              <td>{{ order.paymentStatus }}</td>
              <td>{{ formatCurrency(order.finalAmount) }}</td>
              <td><RouterLink class="ghost-button" :to="`/account/orders/${order.id}`">Chi tiết</RouterLink></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>
