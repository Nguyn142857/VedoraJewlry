<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'

import { cartApi } from '@/modules/cart/api/cartApi'
import type { CartResponse } from '@/modules/cart/types/cart'
import { HttpError } from '@/shared/api/http'
import PageHero from '@/shared/components/base/PageHero.vue'
import { formatCurrency } from '@/shared/utils/format'
import { useCartStore } from '@/stores/cart/useCartStore'
import { useUiStore } from '@/stores/ui/useUiStore'

const cartStore = useCartStore()
const uiStore = useUiStore()

const cart = ref<CartResponse | null>(null)
const isLoading = ref(false)
const busyItemId = ref<number | null>(null)
const errorMessage = ref('')

const itemCount = computed(() => cart.value?.items.reduce((total, item) => total + item.quantity, 0) ?? 0)

function syncCartCount() {
  cartStore.setItemCount(itemCount.value)
}

async function loadCart() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    cart.value = await cartApi.getMyCart()
    syncCartCount()
  } catch (error) {
    errorMessage.value = error instanceof HttpError ? error.message : 'Không thể tải giỏ hàng'
  } finally {
    isLoading.value = false
  }
}

async function updateQuantity(itemId: number, quantity: number) {
  busyItemId.value = itemId

  try {
    cart.value = await cartApi.updateItem(itemId, { quantity })
    syncCartCount()
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể cập nhật số lượng', 'error')
  } finally {
    busyItemId.value = null
  }
}

async function removeItem(itemId: number) {
  busyItemId.value = itemId

  try {
    cart.value = await cartApi.removeItem(itemId)
    syncCartCount()
    uiStore.pushToast('Đã xóa sản phẩm khỏi giỏ hàng', 'success')
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể xóa sản phẩm', 'error')
  } finally {
    busyItemId.value = null
  }
}

async function clearCart() {
  try {
    cart.value = await cartApi.clearCart()
    syncCartCount()
    uiStore.pushToast('Đã xóa toàn bộ giỏ hàng', 'success')
  } catch (error) {
    uiStore.pushToast(error instanceof HttpError ? error.message : 'Không thể xóa giỏ hàng', 'error')
  }
}

onMounted(async () => {
  await loadCart()
})
</script>

<template>
  <div class="stack-md">
    <PageHero eyebrow="Giỏ hàng" title="Giỏ hàng của bạn" description="Kiểm tra số lượng, cập nhật và chuẩn bị cho bước đặt hàng." />

    <section v-if="isLoading" class="surface-card">
      <p>Đang tải giỏ hàng...</p>
    </section>

    <section v-else-if="errorMessage" class="surface-card">
      <p class="error-text">{{ errorMessage }}</p>
    </section>

    <section v-else-if="!cart || cart.items.length === 0" class="surface-card stack-md">
      <p>Giỏ hàng của bạn đang trống.</p>
      <RouterLink class="primary-button inline-button" to="/products">Tiếp tục mua sắm</RouterLink>
    </section>

    <section v-else class="stack-md">
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
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in cart.items" :key="item.id">
                <td>
                  <RouterLink :to="`/products/${item.productSlug}`">{{ item.productName }}</RouterLink>
                </td>
                <td>{{ [item.size, item.color, item.gemstone].filter(Boolean).join(' / ') || 'Mặc định' }}</td>
                <td>
                  <input
                    class="quantity-input"
                    :value="item.quantity"
                    type="number"
                    min="1"
                    :max="item.availableStock"
                    :disabled="busyItemId === item.id"
                    @change="updateQuantity(item.id, Number(($event.target as HTMLInputElement).value))"
                  />
                </td>
                <td>{{ formatCurrency(item.unitPrice) }}</td>
                <td>{{ formatCurrency(item.subtotal) }}</td>
                <td class="action-row">
                  <button class="ghost-button danger-button" type="button" :disabled="busyItemId === item.id" @click="removeItem(item.id)">
                    Xóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="surface-card stack-md">
        <div class="summary-row">
          <strong>Tổng số lượng</strong>
          <span>{{ itemCount }}</span>
        </div>
        <div class="summary-row">
          <strong>Tổng tiền</strong>
          <span>{{ formatCurrency(cart.totalAmount) }}</span>
        </div>
        <div class="action-row">
          <button class="ghost-button" type="button" @click="clearCart()">Xóa toàn bộ</button>
          <RouterLink class="primary-button inline-button" to="/checkout">Tiến hành đặt hàng</RouterLink>
        </div>
      </article>
    </section>
  </div>
</template>
