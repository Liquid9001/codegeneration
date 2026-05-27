<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h1>Dashboard</h1>
        <p class="text-muted">
          Your accounts at ELS Bank
        </p>
      </div>
    </div>

    <div v-if="loading" class="alert alert-info">
      Loading accounts...
    </div>

    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>

    <div class="row">
      <div
        v-for="account in accounts"
        :key="account.id"
        class="col-md-6 col-lg-4 mb-4"
      >
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h5>{{ account.account_type }}</h5>

            <div class="text-muted small mb-3">
              {{ account.iban }}
            </div>

            <h3>
              € {{ Number(account.balance).toFixed(2) }}
            </h3>

            <span
              class="badge"
              :class="account.active ? 'bg-success' : 'bg-danger'"
            >
              {{ account.active ? 'Active' : 'Closed' }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="!loading && accounts.length === 0"
      class="alert alert-warning"
    >
      No accounts found.
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useAuthStore } from '../stores/authStore'
import { getUser } from '../api/userApi'

const authStore = useAuthStore()

const accounts = ref([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true

  try {
    const userId = authStore.user?.sub || authStore.user?.id

    if (!userId) {
      error.value = 'No user id in JWT token.'
      return
    }

    const user = await getUser(userId)

    accounts.value = user.accounts || []

  } catch (e) {
    error.value = 'Failed to load accounts from backend.'
  }

  loading.value = false
})
</script>