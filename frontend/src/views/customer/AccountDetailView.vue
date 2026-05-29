<template>
  <div class="container py-4">
    <div class="els-page-header d-flex align-items-center gap-3">
      <router-link to="/dashboard" class="btn btn-outline-secondary btn-sm" aria-label="Back to dashboard">
        ← Back
      </router-link>
      <div>
        <h1 class="h4 fw-bold mb-0">Account Details</h1>
        <p class="text-muted small mb-0">{{ account?.iban }}</p>
      </div>
    </div>

    <AppSpinner v-if="loading" />

    <template v-else-if="account">
      <!-- Account summary card -->
      <div class="row g-4 mb-4">
        <div class="col-md-6">
          <div class="card h-100 border-0 shadow-sm">
            <div class="card-body p-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span
                  class="badge fs-6 px-3 py-2"
                  :class="account.accountType?.toLowerCase() === 'savings' ? 'badge-savings' : 'badge-checking'"
                >
                  {{ capitalize(account.accountType) }} Account
                </span>
                <span class="badge" :class="account.active ? 'bg-success' : 'bg-secondary'">
                  {{ account.active ? 'Active' : 'Inactive' }}
                </span>
              </div>

              <div class="mb-3">
                <p class="text-muted small mb-1">IBAN</p>
                <span class="els-iban-text">{{ account.iban }}</span>
              </div>

              <div class="mb-3">
                <p class="text-muted small mb-1">Current Balance</p>
                <div class="els-balance-display">{{ formatCurrency(account.balance) }}</div>
              </div>

              <div class="row g-2 text-muted small">
                <div class="col-6">
                  <p class="mb-0">Absolute Limit</p>
                  <strong class="text-body">{{ formatCurrency(account.absoluteTransferLimit) }}</strong>
                </div>
                <div class="col-6">
                  <p class="mb-0">Daily Limit</p>
                  <strong class="text-body">{{ formatCurrency(account.dailyTransferLimit) }}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="col-md-6 d-flex flex-column gap-3">
          <router-link to="/transfer" class="btn btn-primary">⇄ Make a Transfer</router-link>
          <router-link to="/transactions" class="btn btn-outline-primary">📋 View All Transactions</router-link>
          <router-link to="/atm" class="btn btn-outline-secondary">🏧 ATM Deposit / Withdraw</router-link>
        </div>
      </div>

      <!-- Recent transactions -->
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
          <h2 class="h6 fw-semibold mb-0">Recent Transactions</h2>
          <router-link to="/transactions" class="btn btn-link btn-sm p-0 text-decoration-none">
            View all →
          </router-link>
        </div>
        <div class="card-body p-0">
          <AppSpinner v-if="txLoading" size="sm" />
          <div v-else-if="transactions.length === 0" class="els-empty-state">
            <p class="mb-0">No transactions yet.</p>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Date</th>
                  <th>Description</th>
                  <th>Type</th>
                  <th class="text-end">Amount</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tx in transactions.slice(0, 5)" :key="tx.id" class="els-transaction-row">
                  <td class="small text-muted">{{ formatDate(tx.timestamp) }}</td>
                  <td class="small">{{ tx.description || '—' }}</td>
                  <td>
                    <span class="badge bg-light text-dark">{{ capitalize(tx.transactionType) }}</span>
                  </td>
                  <td class="text-end fw-medium" :class="isCredit(tx) ? 'amount-credit' : 'amount-debit'">
                    {{ isCredit(tx) ? '+' : '-' }}{{ formatCurrency(tx.amount) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

    <div v-else class="els-empty-state">
      <p>Account not found.</p>
      <router-link to="/dashboard" class="btn btn-primary">Back to Dashboard</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAccountsStore } from '../../stores/accounts'
import { useTransactionsStore } from '../../stores/transactions'
import { useAuthStore } from '../../stores/auth'
import AppSpinner from '../../components/AppSpinner.vue'

const route = useRoute()
const accountsStore = useAccountsStore()
const txStore = useTransactionsStore()
const auth = useAuthStore()

const account = ref(null)
const transactions = ref([])
const loading = ref(false)
const txLoading = ref(false)

function formatCurrency(v) {
  return new Intl.NumberFormat('nl-NL', { style: 'currency', currency: 'EUR' }).format(v || 0)
}

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('nl-NL', { day: '2-digit', month: 'short', year: 'numeric' })
}

function capitalize(s) {
  return s ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase() : ''
}

function isCredit(tx) {
  return tx.toIban === account.value?.iban
}

onMounted(async () => {
  const accountId = route.params.id
  loading.value = true
  try {
    account.value = await accountsStore.getAccountById(accountId)

    // Load recent transactions for this account
    txLoading.value = true
    try {
      const txList = await txStore.getAllTransactions({
        iban: account.value.iban,
        limit: 10,
        offset: 0,
      })
      transactions.value = txList
    } catch {
      // transactions may not be accessible for customers
    } finally {
      txLoading.value = false
    }
  } finally {
    loading.value = false
  }
})
</script>
