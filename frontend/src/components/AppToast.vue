<template>
  <div
    class="toast-container position-fixed top-0 end-0 p-3"
    style="z-index: 9999"
    aria-live="polite"
    aria-atomic="true"
  >
    <transition-group name="toast-slide" tag="div" class="d-flex flex-column gap-2">
      <div
        v-for="toast in toastStore.toasts"
        :key="toast.id"
        class="toast show align-items-center border-0"
        :class="`text-bg-${toast.type}`"
        role="alert"
        aria-live="assertive"
        aria-atomic="true"
      >
        <div class="d-flex">
          <div class="toast-body d-flex align-items-center gap-2">
            <span v-if="toast.type === 'success'" aria-hidden="true">✓</span>
            <span v-else-if="toast.type === 'danger'" aria-hidden="true">✕</span>
            <span v-else-if="toast.type === 'warning'" aria-hidden="true">⚠</span>
            <span v-else aria-hidden="true">ℹ</span>
            {{ toast.message }}
          </div>
          <button
            type="button"
            class="btn-close btn-close-white me-2 m-auto"
            :aria-label="`Dismiss notification: ${toast.message}`"
            @click="toastStore.removeToast(toast.id)"
          ></button>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useToastStore } from '../stores/toast'
const toastStore = useToastStore()
</script>

<style scoped>
.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.3s ease;
}
.toast-slide-enter-from {
  opacity: 0;
  transform: translateX(100%);
}
.toast-slide-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
