<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import { orderApi } from '@/modules/orders/api/orderApi'
import type { UserOrderDetail } from '@/modules/orders/types/order'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'

const route = useRoute()
const order = ref<UserOrderDetail | null>(null)
const isLoading = ref(false)
const errorMessage = ref('')

async function loadOrder() {
  const orderId = Number(route.params.id)
  if (!orderId) {
    errorMessage.value = 'Mã đơn hàng không hợp lệ'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    order.value = await orderApi.getMyOrderDetail(orderId)
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải chi tiết đơn hàng'
  } finally {
    isLoading.value = false
  }
}

watch(() => route.params.id, async () => loadOrder())

onMounted(async () => {
  await loadOrder()
})
</script>

<template>
  <div class="stack-md">
    <PageHero
      eyebrow="Đơn hàng"
      :title="order ? `Đơn ${order.orderCode}` : 'Chi tiết đơn hàng'"
      description="Kiểm tra thông tin người nhận, sản phẩm và trạng thái xử lý."
    />

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải chi tiết đơn hàng...</p>
    </section>

    <section v-else-if="errorMessage" class="surface-card">
      <p class="error-text">{{ errorMessage }}</p>
    </section>

    <section v-else-if="order" class="stack-md">
      <article class="surface-card detail-meta">
        <p><strong>Mã đơn:</strong> {{ order.orderCode }}</p>
        <p><strong>Trạng thái đơn:</strong> {{ order.orderStatus }}</p>
        <p><strong>Thanh toán:</strong> {{ order.paymentStatus }}</p>
        <p><strong>Người nhận:</strong> {{ order.receiverName }}</p>
        <p><strong>Điện thoại:</strong> {{ order.receiverPhone }}</p>
        <p><strong>Địa chỉ:</strong> {{ order.receiverAddress }}</p>
        <p><strong>Ghi chú:</strong> {{ order.note || 'Không có' }}</p>
        <p><strong>Tổng cuối:</strong> {{ formatCurrency(order.finalAmount) }}</p>
      </article>

      <article class="surface-card">
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
              <tr v-for="item in order.items" :key="item.id">
                <td>{{ item.productName }}</td>
                <td>{{ item.variantInfo || 'Mặc định' }}</td>
                <td>{{ item.quantity }}</td>
                <td>{{ formatCurrency(item.unitPrice) }}</td>
                <td>{{ formatCurrency(item.subtotal) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>
