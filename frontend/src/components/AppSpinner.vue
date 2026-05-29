<template>
  <div class="d-flex justify-content-center align-items-center py-5" :aria-label="label" role="status">
    <div class="spinner-border" :class="[`text-${variant}`, sizeClass]" aria-hidden="true">
      <span class="visually-hidden">{{ label }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  variant: { type: String, default: 'primary' },
  size: { type: String, default: 'md' },
  label: { type: String, default: 'Loading…' },
})

const sizeClass = computed(() => (props.size === 'sm' ? 'spinner-border-sm' : ''))
</script>
