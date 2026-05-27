<template>
  <div class="container py-4">
    <div class="els-page-header">
      <h1 class="h4 fw-bold mb-1">All Transactions</h1>
      <p class="text-muted small mb-0">View and filter all system transactions</p>
    </div>

    <!-- Filters -->
    <div class="card shadow-sm mb-4">
      <div class="card-header bg-white py-3">
        <h2 class="h6 fw-semibold mb-0">Filters</h2>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <label for="ftxStartDate" class="form-label small fw-medium">From date</label>
            <input id="ftxStartDate" v-model="filters.startDate" type="date" class="form-control form-control-sm" />
          </div>
          <div class="col-md-3">
            <label for="ftxEndDate" class="form-label small fw-medium">To date</label>
            <input id="ftxEndDate" v-model="filters.endDate" type="date" class="form-control form-control-sm" />
          </div>
          <div class="col-md-2">
            <label for="ftxMinAmount" class="form-label small fw-medium">Min amount (€)</label>
            <input id="ftxMinAmount" v-model="filters.minAmount" type="number" step="0.01" min="0" class="form-control form-control-sm" placeholder="0.00" />
          </div>
          <div class="col-md-2">
            <label for="ftxMaxAmount" class="form-label small fw-medium">Max amount (€)</label>
            <input id="ftxMaxAmount" v-model="filters.maxAmount" type="number" step="0.01" min="0" class="form-control form-control-sm" placeholder="0.00" />
          </div>
          <div class="col-md-3">
            <label for="ftxIban" class="form-label small fw-medium">IBAN</label>
            <input id="ftxIban" v-model="filters.iban" type="text" class="form-control form-control-sm" placeholder="NL00ELS…" />
          </div>
          <div class="col-md-2">
            <label for="ftxType" class="form-label small fw-medium">Type</label>
            <select id="ftxType" v-model="filters.type" class="form-select form-select-sm">
              <option value="">All types</option>
              <option value="transfer">Transfer</option>
              <option value="deposit">Deposit</option>
              <option value="withdrawal">Withdrawal</option>
            </select>
          </div>
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

    <!-- Results -->
    <div class="card shadow-sm">
      <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
        <h2 class="h6 fw-semibold mb-0">
          Transactions
          <span v-if="filteredTransactions.length > 0" class="text-muted fw-normal small">({{ filteredTransactions.length }})</span>
        </h2>
      </div>

      <div class="card-body p-0">
        <AppSpinner v-if="loading" />

        <div v-else-if="filteredTransactions.length === 0" class="els-empty-state">
          <div class="fs-1 mb-2">📊</div>
          <p class="mb-0">No transactions found.</p>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover table-sm mb-0">
            <thead class="table-light">
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Date & Time</th>
                <th scope="col">Type</th>
                <th scope="col">From IBAN</th>
                <th scope="col">To IBAN</th>
                <th scope="col">Initiated By</th>
                <th scope="col" class="text-end">Amount</th>
                <th scope="col">Status</th>
                <th scope="col">Description</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="tx in pagedTransactions" :key="tx.id" class="els-transaction-row">
                <td class="text-muted small">#{{ tx.id }}</td>
                <td class="small text-nowrap text-muted">{{ formatDate(tx.timestamp) }}</td>
                <td>
                  <span class="badge" :class="typeBadge(tx.transactionType)">
                    {{ capitalize(tx.transactionType) }}
                  </span>
                </td>
                <td class="small"><span class="els-iban-text">{{ tx.fromIban || '—' }}</span></td>
                <td class="small"><span class="els-iban-text">{{ tx.toIban || '—' }}</span></td>
                <td class="small text-muted">{{ tx.initiatedByUserId || '—' }}</td>
                <td class="text-end fw-medium small">{{ formatCurrency(tx.amount) }}</td>
                <td>
                  <span class="badge" :class="statusBadge(tx.status)">
                    {{ tx.status || 'completed' }}
                  </span>
                </td>
                <td class="small text-muted">{{ tx.description || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="filteredTransactions.length > pageSize" class="card-footer bg-white d-flex justify-content-between align-items-center py-3">
        <span class="small text-muted">
          Showing {{ (currentPage - 1) * pageSize + 1 }}–{{ Math.min(currentPage * pageSize, filteredTransactions.length) }}
          of {{ filteredTransactions.length }}
        </span>
        <AppPagination v-model="currentPage" :total-items="filteredTransactions.length" :page-size="pageSize" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useTransactionsStore } from '../../stores/transactions'
import AppSpinner from '../../components/AppSpinner.vue'
import AppPagination from '../../components/AppPagination.vue'

const txStore = useTransactionsStore()

const allTransactions = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 25

const filters = reactive({
  startDate: '',
  endDate: '',
  minAmount: '',
  maxAmount: '',
  iban: '',
  type: '',
})

const filteredTransactions = computed(() => {
  let list = allTransactions.value
  if (filters.type) {
    list = list.filter((t) => t.transactionType?.toLowerCase() === filters.type)
  }
  return list
})

const pagedTransactions = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredTransactions.value.slice(start, start + pageSize)
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

function typeBadge(type) {
  const map = { transfer: 'bg-primary-subtle text-primary-emphasis', deposit: 'bg-success-subtle text-success-emphasis', withdrawal: 'bg-danger-subtle text-danger-emphasis' }
  return map[type?.toLowerCase()] || 'bg-light text-dark'
}

function statusBadge(status) {
  const map = { completed: 'bg-success', failed: 'bg-danger', pending: 'bg-warning text-dark', rejected: 'bg-secondary' }
  return map[status?.toLowerCase()] || 'bg-success'
}

async function loadTransactions(page = 1) {
  loading.value = true
  currentPage.value = page

  try {
    const params = { offset: 0, limit: 500 }
    if (filters.startDate) params.startDate = new Date(filters.startDate).toISOString()
    if (filters.endDate) {
      const end = new Date(filters.endDate)
      end.setHours(23, 59, 59)
      params.endDate = end.toISOString()
    }
    if (filters.minAmount !== '') params.minAmount = parseFloat(filters.minAmount)
    if (filters.maxAmount !== '') params.maxAmount = parseFloat(filters.maxAmount)
    if (filters.iban) params.iban = filters.iban

    allTransactions.value = await txStore.getAllTransactions(params)
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  Object.assign(filters, { startDate: '', endDate: '', minAmount: '', maxAmount: '', iban: '', type: '' })
  loadTransactions(1)
}

onMounted(() => loadTransactions())
</script>
