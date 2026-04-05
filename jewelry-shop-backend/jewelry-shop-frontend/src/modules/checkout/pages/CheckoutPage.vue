<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { cartApi } from '@/modules/cart/api/cartApi'
import type { CartResponse } from '@/modules/cart/types/cart'
import { orderApi } from '@/modules/orders/api/orderApi'
import { profileApi } from '@/modules/profile/api/profileApi'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'
import { useCartStore } from '@/stores/cart/useCartStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const router = useRouter()
const cartStore = useCartStore()
const uiStore = useUiStore()

const cart = ref<CartResponse | null>(null)
const isLoading = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref('')

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  note: '',
  paymentMethod: 'COD' as 'COD' | 'BANKING',
})

const SHIPPING_FEE = 30000
const DISCOUNT_AMOUNT = 0

async function loadCheckoutData() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const [cartResponse, profileResponse] = await Promise.all([cartApi.getMyCart(), profileApi.getMyProfile()])
    cart.value = cartResponse
    form.receiverName = profileResponse.fullName
    form.receiverPhone = profileResponse.phone ?? ''
    form.receiverAddress = profileResponse.address ?? ''
    cartStore.setItemCount(cartResponse.items.reduce((total, item) => total + item.quantity, 0))
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải dữ liệu thanh toán'
  } finally {
    isLoading.value = false
  }
}

async function handleSubmit() {
  isSubmitting.value = true
  errorMessage.value = ''

  try {
    const order = await orderApi.createOrder(form)
    cartStore.setItemCount(0)
    uiStore.pushToast('Đặt hàng thành công', 'success')
    await router.push(`/account/orders/${order.id}`)
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tạo đơn hàng'
  } finally {
    isSubmitting.value = false
  }
}

onMounted(async () => {
  await loadCheckoutData()
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Thanh toán" title="Xác nhận đơn hàng" description="Kiểm tra lại giỏ hàng và hoàn tất thông tin người nhận." />

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải dữ liệu thanh toán...</p>
    </section>

    <section v-else-if="errorMessage" class="surface-card">
      <p class="error-text">{{ errorMessage }}</p>
    </section>

    <section v-else-if="!cart || cart.items.length === 0" class="surface-card">
      <p>Giỏ hàng đang trống. Vui lòng thêm sản phẩm trước khi thanh toán.</p>
    </section>

    <section v-else class="admin-grid checkout-grid">
      <article class="surface-card stack-md">
        <h2 class="section-title">Thông tin nhận hàng</h2>
        <form class="stack-md" @submit.prevent="handleSubmit()">
          <label class="field">
            <span>Người nhận</span>
            <input v-model.trim="form.receiverName" type="text" required />
          </label>
          <label class="field">
            <span>Số điện thoại</span>
            <input v-model.trim="form.receiverPhone" type="text" required />
          </label>
          <label class="field">
            <span>Địa chỉ</span>
            <textarea v-model.trim="form.receiverAddress" rows="4" required />
          </label>
          <label class="field">
            <span>Phương thức thanh toán</span>
            <select v-model="form.paymentMethod">
              <option value="COD">COD</option>
              <option value="BANKING">BANKING</option>
            </select>
          </label>
          <label class="field">
            <span>Ghi chú</span>
            <textarea v-model.trim="form.note" rows="3" />
          </label>
          <button class="primary-button" type="submit" :disabled="isSubmitting">
            {{ isSubmitting ? 'Đang đặt hàng...' : 'Đặt hàng ngay' }}
          </button>
        </form>
      </article>

      <article class="surface-card stack-md">
        <h2 class="section-title">Tóm tắt đơn hàng</h2>
        <div class="table-wrap">
          <table class="admin-table">
            <tbody>
              <tr v-for="item in cart.items" :key="item.id">
                <td>{{ item.productName }}</td>
                <td>{{ item.quantity }}</td>
                <td>{{ formatCurrency(item.subtotal) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="summary-row">
          <strong>Tạm tính</strong>
          <span>{{ formatCurrency(cart.totalAmount) }}</span>
        </div>
        <div class="summary-row">
          <strong>Phí ship</strong>
          <span>{{ formatCurrency(SHIPPING_FEE) }}</span>
        </div>
        <div class="summary-row">
          <strong>Giảm giá</strong>
          <span>{{ formatCurrency(DISCOUNT_AMOUNT) }}</span>
        </div>
        <div class="summary-row">
          <strong>Tổng thanh toán</strong>
          <span>{{ formatCurrency(cart.totalAmount + SHIPPING_FEE - DISCOUNT_AMOUNT) }}</span>
        </div>
      </article>
    </section>
  </div>
</template>
