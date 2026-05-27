<template>
  <div class="container py-4">
    <!-- Header -->
    <div class="els-page-header d-flex align-items-center gap-3 flex-wrap">
      <router-link to="/employee/customers" class="btn btn-outline-secondary btn-sm" aria-label="Back to customers">
        ← Back
      </router-link>
      <div class="flex-grow-1">
        <h1 class="h4 fw-bold mb-0">
          {{ customer ? `${customer.firstName} ${customer.lastName}` : 'Customer Detail' }}
        </h1>
        <p v-if="customer" class="text-muted small mb-0">{{ customer.email }}</p>
      </div>
      <div v-if="customer && !customer.approved">
        <button class="btn btn-success btn-sm" @click="openApproveModal" :disabled="approving">
          ✓ Approve Customer
        </button>
      </div>
    </div>

    <AppSpinner v-if="loading" />

    <template v-else-if="customer">
      <div class="row g-4">
        <!-- Customer info -->
        <div class="col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-header bg-white py-3">
              <h2 class="h6 fw-semibold mb-0">Customer Information</h2>
            </div>
            <div class="card-body">
              <dl class="row small mb-0">
                <dt class="col-5 text-muted">Full Name</dt>
                <dd class="col-7">{{ customer.firstName }} {{ customer.lastName }}</dd>

                <dt class="col-5 text-muted">Email</dt>
                <dd class="col-7 text-break">{{ customer.email }}</dd>

                <dt class="col-5 text-muted">Phone</dt>
                <dd class="col-7">{{ customer.phoneNumber || '—' }}</dd>

                <dt class="col-5 text-muted">BSN</dt>
                <dd class="col-7">{{ customer.bsn || '—' }}</dd>

                <dt class="col-5 text-muted">Role</dt>
                <dd class="col-7">
                  <span class="badge bg-light text-dark">{{ customer.role }}</span>
                </dd>

                <dt class="col-5 text-muted">Status</dt>
                <dd class="col-7">
                  <span class="badge" :class="customer.approved ? 'bg-success' : 'bg-warning text-dark'">
                    {{ customer.approved ? 'Approved' : 'Pending' }}
                  </span>
                </dd>

                <dt class="col-5 text-muted">Registered</dt>
                <dd class="col-7">{{ formatDate(customer.createdAt) }}</dd>
              </dl>
            </div>
          </div>
        </div>

        <!-- Accounts -->
        <div class="col-lg-8">
          <div v-if="!customer.accounts?.length" class="alert alert-info">
            This customer has no accounts yet.
            <span v-if="!customer.approved"> Approve the customer to create accounts.</span>
          </div>

          <div v-else class="d-flex flex-column gap-3">
            <div v-for="account in customer.accounts" :key="account.id" class="card shadow-sm"
              :class="account.accountType?.toLowerCase() === 'savings' ? 'els-account-savings' : 'els-account-checking'">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-3">
                  <div>
                    <span class="badge fs-6 px-3 py-2"
                      :class="account.accountType?.toLowerCase() === 'savings' ? 'badge-savings' : 'badge-checking'">
                      {{ capitalize(account.accountType) }}
                    </span>
                  </div>
                  <div class="d-flex gap-2">
                    <span class="badge" :class="account.active ? 'bg-success' : 'bg-secondary'">
                      {{ account.active ? 'Active' : 'Closed' }}
                    </span>
                    <button v-if="account.active" class="btn btn-outline-danger btn-sm"
                      @click="openCloseModal(account)" :disabled="closingAccountId === account.id">
                      Close Account
                    </button>
                  </div>
                </div>

                <div class="row g-3 align-items-center">
                  <div class="col-md-6">
                    <p class="text-muted small mb-1">IBAN</p>
                    <span class="els-iban-text">{{ account.iban }}</span>
                  </div>
                  <div class="col-md-6">
                    <p class="text-muted small mb-1">Balance</p>
                    <div class="fs-4 fw-bold">{{ formatCurrency(account.balance) }}</div>
                  </div>
                </div>

                <!-- Limits -->
                <div class="row g-3 mt-1">
                  <div class="col-md-5">
                    <label class="form-label small fw-medium mb-1">Absolute Limit (€)</label>
                    <div class="input-group input-group-sm">
                      <span class="input-group-text">€</span>
                      <input
                        type="number"
                        class="form-control"
                        v-model="limitEdits[account.id].absoluteLimit"
                        step="100"
                        min="0"
                        :aria-label="`Absolute limit for ${account.iban}`"
                      />
                    </div>
                  </div>
                  <div class="col-md-5">
                    <label class="form-label small fw-medium mb-1">Daily Limit (€)</label>
                    <div class="input-group input-group-sm">
                      <span class="input-group-text">€</span>
                      <input
                        type="number"
                        class="form-control"
                        v-model="limitEdits[account.id].dailyLimit"
                        step="100"
                        min="0"
                        :aria-label="`Daily limit for ${account.iban}`"
                      />
                    </div>
                  </div>
                  <div class="col-md-2 d-flex align-items-end">
                    <button
                      class="btn btn-primary btn-sm w-100"
                      :disabled="savingAccountId === account.id"
                      @click="saveLimits(account)"
                    >
                      <span v-if="savingAccountId === account.id" class="spinner-border spinner-border-sm" aria-hidden="true"></span>
                      <span v-else>Save</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Transfer between customers -->
      <div class="card shadow-sm mt-4">
        <div class="card-header bg-white py-3">
          <h2 class="h6 fw-semibold mb-0">Transfer Funds</h2>
        </div>
        <div class="card-body">
          <div v-if="transferSuccess" class="alert alert-success alert-dismissible" role="status">
            Transfer successful!
            <button class="btn-close" @click="transferSuccess = false" aria-label="Close"></button>
          </div>
          <div v-if="transferError" class="alert alert-danger alert-dismissible" role="alert">
            {{ transferError }}
            <button class="btn-close" @click="transferError = ''" aria-label="Close"></button>
          </div>

          <form @submit.prevent="handleTransfer" class="row g-3">
            <div class="col-md-4">
              <label class="form-label small fw-medium">From Account (this customer)</label>
              <select v-model="transfer.fromAccount" class="form-select form-select-sm" :class="{ 'is-invalid': transferErrors.from }">
                <option value="">Select…</option>
                <option v-for="acc in customer.accounts?.filter(a => a.active && a.accountType?.toLowerCase() === 'checking')" :key="acc.id" :value="acc">
                  {{ acc.iban }} ({{ formatCurrency(acc.balance) }})
                </option>
              </select>
              <div v-if="transferErrors.from" class="invalid-feedback">Required.</div>
            </div>
            <div class="col-md-4">
              <label class="form-label small fw-medium">To IBAN</label>
              <input v-model="transfer.toIban" type="text" class="form-control form-control-sm" :class="{ 'is-invalid': transferErrors.toIban }" placeholder="NL00ELS…" />
              <div v-if="transferErrors.toIban" class="invalid-feedback">Enter a valid IBAN.</div>
            </div>
            <div class="col-md-2">
              <label class="form-label small fw-medium">Amount (€)</label>
              <input v-model="transfer.amount" type="number" step="0.01" min="0.01" class="form-control form-control-sm" :class="{ 'is-invalid': transferErrors.amount }" placeholder="0.00" />
              <div v-if="transferErrors.amount" class="invalid-feedback">Required.</div>
            </div>
            <div class="col-md-2 d-flex align-items-end">
              <button type="submit" class="btn btn-primary btn-sm w-100" :disabled="transferring">
                <span v-if="transferring" class="spinner-border spinner-border-sm" aria-hidden="true"></span>
                <span v-else>Transfer</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Transaction History -->
      <div class="card shadow-sm mt-4">
        <div class="card-header bg-white py-3">
          <h2 class="h6 fw-semibold mb-0">Transaction History</h2>
        </div>
        <div class="card-body p-0">
          <AppSpinner v-if="txLoading" size="sm" />
          <div v-else-if="transactions.length === 0" class="els-empty-state">
            <p class="mb-0">No transactions found for this customer.</p>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-hover table-sm mb-0">
              <thead class="table-light">
                <tr>
                  <th>Date</th>
                  <th>Description</th>
                  <th>Type</th>
                  <th>From</th>
                  <th>To</th>
                  <th class="text-end">Amount</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tx in transactions.slice(0, 20)" :key="tx.id" class="els-transaction-row">
                  <td class="small text-muted">{{ formatDate(tx.timestamp) }}</td>
                  <td class="small">{{ tx.description || '—' }}</td>
                  <td><span class="badge bg-light text-dark small">{{ capitalize(tx.transactionType) }}</span></td>
                  <td><span class="els-iban-text">{{ tx.fromIban || '—' }}</span></td>
                  <td><span class="els-iban-text">{{ tx.toIban || '—' }}</span></td>
                  <td class="text-end fw-medium small">{{ formatCurrency(tx.amount) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

    <div v-else class="els-empty-state">
      <p>Customer not found.</p>
      <router-link to="/employee/customers" class="btn btn-primary">Back to Customers</router-link>
    </div>

    <!-- Close Account Confirmation Modal -->
    <div class="modal fade" id="closeAccountModal" tabindex="-1" aria-labelledby="closeModalLabel" aria-modal="true" ref="closeModalEl">
      <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
          <div class="modal-header border-0">
            <h3 class="modal-title h6 fw-bold text-danger" id="closeModalLabel">Close Account?</h3>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="text-muted small mb-0">
              This will permanently close the account
              <strong>{{ accountToClose?.iban }}</strong>. This action cannot be undone.
            </p>
          </div>
          <div class="modal-footer border-0">
            <button class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
            <button class="btn btn-danger btn-sm" :disabled="closingAccountId !== null" @click="confirmCloseAccount">
              <span v-if="closingAccountId !== null" class="spinner-border spinner-border-sm me-1" aria-hidden="true"></span>
              Close Account
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Approve Modal -->
    <div class="modal fade" id="approveCustomerModal" tabindex="-1" aria-labelledby="approveCustomerLabel" aria-modal="true" ref="approveModalEl">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h3 class="modal-title h6 fw-bold" id="approveCustomerLabel">Approve Customer</h3>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="text-muted small mb-3">
              Approving will create a checking and savings account for this customer.
            </p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
            <button class="btn btn-success" :disabled="approving" @click="confirmApprove">
              <span v-if="approving" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
              Approve
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import * as bootstrap from 'bootstrap'
import { useUsersStore } from '../../stores/users'
import { useAccountsStore } from '../../stores/accounts'
import { useTransactionsStore } from '../../stores/transactions'
import { useAuthStore } from '../../stores/auth'
import AppSpinner from '../../components/AppSpinner.vue'

const route = useRoute()
const usersStore = useUsersStore()
const accountsStore = useAccountsStore()
const txStore = useTransactionsStore()
const auth = useAuthStore()

const customer = ref(null)
const loading = ref(false)
const transactions = ref([])
const txLoading = ref(false)
const savingAccountId = ref(null)
const closingAccountId = ref(null)
const approving = ref(false)
const accountToClose = ref(null)

const limitEdits = reactive({})

const closeModalEl = ref(null)
const approveModalEl = ref(null)
let closeModal = null
let approveModal = null

// Transfer state
const transfer = reactive({ fromAccount: '', toIban: '', amount: '' })
const transferErrors = reactive({})
const transferring = ref(false)
const transferSuccess = ref(false)
const transferError = ref('')

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

function initLimitEdits(accounts) {
  accounts?.forEach((acc) => {
    limitEdits[acc.id] = {
      absoluteLimit: acc.absoluteTransferLimit || 1000,
      dailyLimit: acc.dailyTransferLimit || 5000,
    }
  })
}

async function saveLimits(account) {
  savingAccountId.value = account.id
  try {
    await accountsStore.updateAccount(account.id, {
      ...account,
      absoluteTransferLimit: parseFloat(limitEdits[account.id].absoluteLimit),
      dailyTransferLimit: parseFloat(limitEdits[account.id].dailyLimit),
    })
    // Refresh customer data
    customer.value = await usersStore.getUserById(route.params.id)
    initLimitEdits(customer.value.accounts)
  } finally {
    savingAccountId.value = null
  }
}

function openCloseModal(account) {
  accountToClose.value = account
  closeModal?.show()
}

async function confirmCloseAccount() {
  if (!accountToClose.value) return
  closingAccountId.value = accountToClose.value.id
  try {
    await accountsStore.deleteAccount(accountToClose.value.id)
    closeModal?.hide()
    customer.value = await usersStore.getUserById(route.params.id)
    initLimitEdits(customer.value.accounts)
  } finally {
    closingAccountId.value = null
    accountToClose.value = null
  }
}

function openApproveModal() {
  approveModal?.show()
}

async function confirmApprove() {
  approving.value = true
  try {
    await usersStore.approveUser(customer.value.id)
    approveModal?.hide()
    customer.value = await usersStore.getUserById(route.params.id)
    initLimitEdits(customer.value.accounts)
    loadTransactions()
  } finally {
    approving.value = false
  }
}

async function handleTransfer() {
  Object.keys(transferErrors).forEach((k) => delete transferErrors[k])
  if (!transfer.fromAccount) transferErrors.from = true
  if (!transfer.toIban?.trim()) transferErrors.toIban = true
  const amt = parseFloat(transfer.amount)
  if (!transfer.amount || isNaN(amt) || amt <= 0) transferErrors.amount = true
  if (Object.keys(transferErrors).length > 0) return

  transferring.value = true
  transferSuccess.value = false
  transferError.value = ''
  try {
    await txStore.createTransaction({
      from_iban: transfer.fromAccount.iban,
      to_iban: transfer.toIban.trim(),
      initiated_by_user_id: auth.currentUser?.id,
      amount: amt,
      transaction_type: 'transfer',
      currency: 'EUR',
      description: 'Employee transfer',
    })
    transferSuccess.value = true
    transfer.fromAccount = ''
    transfer.toIban = ''
    transfer.amount = ''
    customer.value = await usersStore.getUserById(route.params.id)
    initLimitEdits(customer.value.accounts)
    await loadTransactions()
  } catch (err) {
    transferError.value = err.response?.data?.message || 'Transfer failed.'
  } finally {
    transferring.value = false
  }
}

async function loadTransactions() {
  if (!customer.value?.accounts?.length) return
  txLoading.value = true
  try {
    const firstIban = customer.value.accounts[0]?.iban
    if (firstIban) {
      transactions.value = await txStore.getAllTransactions({ iban: firstIban, limit: 50, offset: 0 })
    }
  } finally {
    txLoading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    customer.value = await usersStore.getUserById(route.params.id)
    initLimitEdits(customer.value?.accounts)
    await loadTransactions()
  } finally {
    loading.value = false
  }

  closeModal = new bootstrap.Modal(closeModalEl.value)
  approveModal = new bootstrap.Modal(approveModalEl.value)
})
</script>
