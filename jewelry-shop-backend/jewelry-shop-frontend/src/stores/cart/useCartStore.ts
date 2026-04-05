import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { cartApi } from '@/modules/cart/api/cartApi'

export const useCartStore = defineStore('cart', () => {
  const itemCount = ref(0)
  const isLoading = ref(false)

  const hasItems = computed(() => itemCount.value > 0)

  function setItemCount(count: number) {
    itemCount.value = Math.max(0, count)
  }

  async function hydrateCart() {
    isLoading.value = true

    try {
      const cart = await cartApi.getMyCart()
      itemCount.value = cart.items.reduce((total, item) => total + item.quantity, 0)
    } catch {
      itemCount.value = 0
    } finally {
      isLoading.value = false
    }
  }

  return {
    hasItems,
    isLoading,
    itemCount,
    hydrateCart,
    setItemCount,
  }
})
