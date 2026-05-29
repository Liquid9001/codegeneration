<template>
  <nav v-if="totalPages > 1" :aria-label="ariaLabel">
    <ul class="pagination pagination-sm mb-0 flex-wrap">
      <li class="page-item" :class="{ disabled: modelValue === 1 }">
        <button class="page-link" :disabled="modelValue === 1" @click="emit('update:modelValue', 1)" aria-label="First page">
          «
        </button>
      </li>
      <li class="page-item" :class="{ disabled: modelValue === 1 }">
        <button class="page-link" :disabled="modelValue === 1" @click="emit('update:modelValue', modelValue - 1)" aria-label="Previous page">
          ‹
        </button>
      </li>

      <li
        v-for="page in visiblePages"
        :key="page"
        class="page-item"
        :class="{ active: page === modelValue, disabled: page === '…' }"
        :aria-current="page === modelValue ? 'page' : undefined"
      >
        <span v-if="page === '…'" class="page-link">…</span>
        <button v-else class="page-link" @click="emit('update:modelValue', page)">
          {{ page }}
        </button>
      </li>

      <li class="page-item" :class="{ disabled: modelValue === totalPages }">
        <button class="page-link" :disabled="modelValue === totalPages" @click="emit('update:modelValue', modelValue + 1)" aria-label="Next page">
          ›
        </button>
      </li>
      <li class="page-item" :class="{ disabled: modelValue === totalPages }">
        <button class="page-link" :disabled="modelValue === totalPages" @click="emit('update:modelValue', totalPages)" aria-label="Last page">
          »
        </button>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Number, required: true },
  totalItems: { type: Number, required: true },
  pageSize: { type: Number, default: 20 },
  ariaLabel: { type: String, default: 'Page navigation' },
})

const emit = defineEmits(['update:modelValue'])

const totalPages = computed(() => Math.ceil(props.totalItems / props.pageSize) || 1)

const visiblePages = computed(() => {
  const total = totalPages.value
  const current = props.modelValue
  const pages = []

  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
    return pages
  }

  pages.push(1)
  if (current > 3) pages.push('…')
  const start = Math.max(2, current - 1)
  const end = Math.min(total - 1, current + 1)
  for (let i = start; i <= end; i++) pages.push(i)
  if (current < total - 2) pages.push('…')
  pages.push(total)

  return pages
})
</script>
