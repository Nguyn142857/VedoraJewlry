import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

interface Toast {
  id: number
  title: string
  tone: 'info' | 'success' | 'error'
}

export const useUiStore = defineStore('ui', () => {
  const isMobileNavOpen = ref(false)
  const toasts = ref<Toast[]>([])

  const hasToasts = computed(() => toasts.value.length > 0)

  function toggleMobileNav() {
    isMobileNavOpen.value = !isMobileNavOpen.value
  }

  function closeMobileNav() {
    isMobileNavOpen.value = false
  }

  function pushToast(title: string, tone: Toast['tone'] = 'info') {
    const id = Date.now()
    toasts.value.push({ id, title, tone })
    window.setTimeout(() => {
      toasts.value = toasts.value.filter((toast) => toast.id !== id)
    }, 3000)
  }

  return {
    hasToasts,
    isMobileNavOpen,
    toasts,
    closeMobileNav,
    pushToast,
    toggleMobileNav,
  }
})
