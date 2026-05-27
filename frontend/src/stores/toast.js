import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useToastStore = defineStore('toast', () => {
  const toasts = ref([])
  let nextId = 0

  function addToast(message, type = 'info', duration = 4000) {
    const id = nextId++
    toasts.value.push({ id, message, type })
    setTimeout(() => removeToast(id), duration)
  }

  function removeToast(id) {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  function success(message) {
    addToast(message, 'success')
  }

  function error(message) {
    addToast(message, 'danger', 6000)
  }

  function info(message) {
    addToast(message, 'info')
  }

  function warning(message) {
    addToast(message, 'warning')
  }

  return { toasts, success, error, info, warning, removeToast }
})
