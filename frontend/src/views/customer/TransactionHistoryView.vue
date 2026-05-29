<template>
  <div class="container py-4">
    <div class="els-page-header d-flex align-items-start justify-content-between flex-wrap gap-2">
      <div>
        <h1 class="h4 fw-bold mb-1">Transaction History</h1>
        <p class="text-muted small mb-0">View and filter your transactions</p>
      </div>
    </div>

    <!-- Filters card -->
    <div class="card shadow-sm mb-4">
      <div class="card-header bg-white py-3">
        <h2 class="h6 fw-semibold mb-0">Filters</h2>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <!-- Account / IBAN -->
          <div class="col-md-4">
            <label for="filterIban" class="form-label small fw-medium">Account</label>
            <select id="filterIban" v-model="filters.iban" class="form-select form-select-sm">
              <option value="">All accounts</option>
              <option v-for="acc in ownAccounts" :key="acc.id" :value="acc.iban">
                {{ capitalize(acc.accountType) }} — {{ acc.iban }}
              </option>
            </select>
          </div>

          <!-- Date range -->
          <div class="col-md-4">
            <label for="filterStartDate" class="form-label small fw-medium">From date</label>
            <input id="filterStartDate" v-model="filters.startDate" type="date" class="form-control form-control-sm" />
          </div>
          <div class="col-md-4">
            <label for="filterEndDate" class="form-label small fw-medium">To date</label>
            <input id="filterEndDate" v-model="filters.endDate" type="date" class="form-control form-control-sm" />
          </div>

          <!-- Amount range -->
          <div class="col-md-3">
            <label for="filterMinAmount" class="form-label small fw-medium">Min amount (€)</label>
            <input id="filterMinAmount" v-model="filters.minAmount" type="number" step="0.01" min="0" class="form-control form-control-sm" placeholder="0.00" />
          </div>
          <div class="col-md-3">
            <label for="filterMaxAmount" class="form-label small fw-medium">Max amount (€)</label>
            <input id="filterMaxAmount" v-model="filters.maxAmount" type="number" step="0.01" min="0" class="form-control form-control-sm" placeholder="0.00" />
          </div>

          <!-- Actions -->
          <div class="col-12 d-flex gap-2 align-items-end">
            <button class="btn btn-primary btn-sm" :disabled="loading" @click="loadTransactions(1)">
              <span v-if="loading" class="spinner-border spinner-border-sm me-1" aria-hidden="true"></span>
              Apply Filters
            </button>
            <button class="btn btn-outline-secondary btn-sm" @click="resetFilters">Reset</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Info notice for customers -->
    <div v-if="accessDenied" class="alert alert-warning" role="alert">
      Transaction history is currently unavailable. Please contact support.
    </div>

    <!-- Results -->
    <div class="card shadow-sm">
      <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
        <h2 class="h6 fw-semibold mb-0">
          Transactions
          <span v-if="transactions.length > 0" class="text-muted fw-normal small">({{ transactions.length }} shown)</span>
        </h2>
      </div>

      <div class="card-body p-0">
        <AppSpinner v-if="loading" />

        <div v-else-if="transactions.length === 0 && !loading" class="els-empty-state">
          <div class="fs-1 mb-2">📋</div>
          <p class="mb-0">No transactions found.</p>
          <p class="small text-muted">Try adjusting your filters.</p>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th scope="col">Date & Time</th>
                <th scope="col">Description</th>
                <th scope="col">Type</th>
                <th scope="col">From IBAN</th>
                <th scope="col">To IBAN</th>
                <th scope="col" class="text-end">Amount</th>
                <th scope="col">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="tx in pagedTransactions" :key="tx.id" class="els-transaction-row">
                <td class="small text-muted text-nowrap">{{ formatDate(tx.timestamp) }}</td>
                <td class="small">{{ tx.description || '—' }}</td>
                <td>
                  <span class="badge bg-light text-dark">{{ capitalize(tx.transactionType) }}</span>
                </td>
                <td class="small">
                  <span class="els-iban-text">{{ tx.fromIban || '—' }}</span>
                </td>
                <td class="small">
                  <span class="els-iban-text">{{ tx.toIban || '—' }}</span>
                </td>
                <td class="text-end fw-medium" :class="isCreditForUser(tx) ? 'amount-credit' : 'amount-debit'">
                  {{ isCreditForUser(tx) ? '+' : '-' }}{{ formatCurrency(tx.amount) }}
                </td>
                <td>
                  <span class="badge" :class="statusBadge(tx.status)">
                    {{ tx.status || 'completed' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="transactions.length > pageSize" class="card-footer bg-white d-flex justify-content-between align-items-center py-3">
        <span class="small text-muted">
          Showing {{ (currentPage - 1) * pageSize + 1 }}–{{ Math.min(currentPage * pageSize, transactions.length) }}
          of {{ transactions.length }}
        </span>
        <AppPagination v-model="currentPage" :total-items="transactions.length" :page-size="pageSize" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useTransactionsStore } from '../../stores/transactions'
import AppSpinner from '../../components/AppSpinner.vue'
import AppPagination from '../../components/AppPagination.vue'

const auth = useAuthStore()
const txStore = useTransactionsStore()

const transactions = ref([])
const loading = ref(false)
const accessDenied = ref(false)
const currentPage = ref(1)
const pageSize = 20

const filters = reactive({
  iban: '',
  startDate: '',
  endDate: '',
  minAmount: '',
  maxAmount: '',
})

const ownAccounts = computed(() => auth.currentUser?.accounts || [])
const ownIbans = computed(() => ownAccounts.value.map((a) => a.iban))

function isCreditForUser(tx) {
  return ownIbans.value.includes(tx.toIban)
}

const pagedTransactions = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return transactions.value.slice(start, start + pageSize)
})

function formatCurrency(v) {
  return new Intl.NumberFormat('nl-NL', { style: 'currency', currency: 'EUR' }).format(v || 0)
}

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleString('nl-NL', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}

function capitalize(s) {
  return s ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase() : ''
}

function statusBadge(status) {
  const map = { completed: 'bg-success', failed: 'bg-danger', pending: 'bg-warning text-dark', rejected: 'bg-secondary' }
  return map[status?.toLowerCase()] || 'bg-success'
}

async function loadTransactions(page = 1) {
  loading.value = false
  accessDenied.value = false
  loading.value = true
  currentPage.value = page

  try {
    const params = { offset: 0, limit: 100 }
    if (filters.iban) params.iban = filters.iban
    if (filters.startDate) params.startDate = new Date(filters.startDate).toISOString()
    if (filters.endDate) {
      const end = new Date(filters.endDate)
      end.setHours(23, 59, 59)
      params.endDate = end.toISOString()
    }
    if (filters.minAmount !== '') params.minAmount = parseFloat(filters.minAmount)
    if (filters.maxAmount !== '') params.maxAmount = parseFloat(filters.maxAmount)

    // For customers: try to use their first account's IBAN if none selected
    if (!params.iban && ownIbans.value.length > 0) {
      params.iban = ownIbans.value[0]
    }

    transactions.value = await txStore.getAllTransactions(params)
  } catch (err) {
    if (err.response?.status === 403) {
      accessDenied.value = true
    }
    transactions.value = []
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  Object.assign(filters, { iban: '', startDate: '', endDate: '', minAmount: '', maxAmount: '' })
  loadTransactions(1)
}

onMounted(async () => {
  await auth.refreshUser()
  loadTransactions()
})
</script>
