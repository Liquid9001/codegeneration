<template>
  <div class="container py-4">
    <!-- Page header -->
    <div class="els-page-header d-flex align-items-center justify-content-between flex-wrap gap-2">
      <div>
        <h1 class="h4 fw-bold mb-0">My Dashboard</h1>
        <p class="text-muted small mb-0">Welcome back, {{ userName }}</p>
      </div>
      <div class="d-flex gap-2 flex-wrap">
        <router-link to="/transfer" class="btn btn-primary btn-sm">
          ⇄ Transfer Money
        </router-link>
        <router-link to="/atm" class="btn btn-outline-secondary btn-sm">
          🏧 ATM
        </router-link>
      </div>
    </div>

    <AppSpinner v-if="loading" />

    <template v-else>
      <!-- Combined balance -->
      <div class="card mb-4 border-0 bg-primary text-white shadow-sm">
        <div class="card-body p-4">
          <p class="small opacity-75 mb-1">Total Balance</p>
          <div class="els-balance-display text-white">{{ formatCurrency(totalBalance) }}</div>
          <p class="small opacity-75 mt-1 mb-0">Across {{ accounts.length }} account{{ accounts.length !== 1 ? 's' : '' }}</p>
        </div>
      </div>

      <!-- Account cards -->
      <h2 class="h6 fw-semibold text-muted text-uppercase mb-3 letter-spacing">Accounts</h2>

      <div v-if="accounts.length === 0" class="els-empty-state">
        <p>No accounts found.</p>
      </div>

      <div class="row g-3 mb-4">
        <div v-for="account in accounts" :key="account.id" class="col-md-6">
          <div
            class="card els-account-card h-100"
            :class="account.accountType?.toLowerCase() === 'savings' ? 'els-account-savings' : 'els-account-checking'"
          >
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <span
                  class="badge"
                  :class="account.accountType?.toLowerCase() === 'savings' ? 'badge-savings' : 'badge-checking'"
                >
                  {{ capitalize(account.accountType) }}
                </span>
                <span class="small text-muted">{{ account.active ? 'Active' : 'Inactive' }}</span>
              </div>
              <div class="mb-2">
                <div class="els-iban-text">{{ account.iban }}</div>
              </div>
              <div class="d-flex align-items-end justify-content-between mt-3">
                <div>
                  <p class="text-muted small mb-0">Balance</p>
                  <p class="fs-4 fw-bold mb-0">{{ formatCurrency(account.balance) }}</p>
                </div>
                <router-link :to="`/accounts/${account.id}`" class="btn btn-outline-primary btn-sm">
                  View details
                </router-link>
              </div>
            </div>
            <div class="card-footer bg-transparent border-top small text-muted d-flex gap-3">
              <span>Absolute limit: {{ formatCurrency(account.absoluteTransferLimit) }}</span>
              <span>Daily limit: {{ formatCurrency(account.dailyTransferLimit) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick actions -->
      <h2 class="h6 fw-semibold text-muted text-uppercase mb-3">Quick Actions</h2>
      <div class="row g-3">
        <div class="col-6 col-md-3">
          <router-link to="/transfer" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">⇄</div>
              <div class="fw-medium small">Transfer</div>
            </div>
          </router-link>
        </div>
        <div class="col-6 col-md-3">
          <router-link to="/transactions" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">📋</div>
              <div class="fw-medium small">History</div>
            </div>
          </router-link>
        </div>
        <div class="col-6 col-md-3">
          <router-link to="/atm" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">🏧</div>
              <div class="fw-medium small">ATM</div>
            </div>
          </router-link>
        </div>
        <div class="col-6 col-md-3">
          <router-link :to="accounts[0] ? `/accounts/${accounts[0].id}` : '/dashboard'" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">🏦</div>
              <div class="fw-medium small">My Account</div>
            </div>
          </router-link>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import AppSpinner from '../../components/AppSpinner.vue'

const auth = useAuthStore()
const loading = ref(false)

const accounts = computed(() => auth.currentUser?.accounts || [])

const userName = computed(() => {
  const u = auth.currentUser
  if (!u) return ''
  if (u.firstName) return u.firstName
  return u.email?.split('@')[0] || ''
})

const totalBalance = computed(() =>
  accounts.value.reduce((sum, a) => sum + (Number(a.balance) || 0), 0)
)

function formatCurrency(amount) {
  return new Intl.NumberFormat('nl-NL', { style: 'currency', currency: 'EUR' }).format(amount || 0)
}

function capitalize(str) {
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

onMounted(async () => {
  loading.value = true
  try {
    await auth.refreshUser()
  } finally {
    loading.value = false
  }
})
</script>
