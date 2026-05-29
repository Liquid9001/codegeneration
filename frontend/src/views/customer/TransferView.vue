<template>
  <div class="container py-4">
    <div class="els-page-header">
      <h1 class="h4 fw-bold mb-1">Transfer Money</h1>
      <p class="text-muted small mb-0">Transfer between your accounts or to another customer</p>
    </div>

    <div class="row justify-content-center">
      <div class="col-lg-7">
        <!-- Transfer type selector -->
        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <div class="btn-group w-100" role="group" aria-label="Transfer type">
              <input type="radio" class="btn-check" id="typeOwn" value="own" v-model="transferType" />
              <label class="btn btn-outline-primary" for="typeOwn">Between My Accounts</label>

              <input type="radio" class="btn-check" id="typeExternal" value="external" v-model="transferType" />
              <label class="btn btn-outline-primary" for="typeExternal">To Another Customer</label>
            </div>
          </div>
        </div>

        <!-- Success -->
        <div v-if="success" class="alert alert-success alert-dismissible" role="status">
          <strong>Transfer successful!</strong> The transaction has been completed.
          <button class="btn-close" @click="success = false" aria-label="Close"></button>
        </div>

        <!-- Error -->
        <div v-if="errorMessage" class="alert alert-danger alert-dismissible" role="alert">
          {{ errorMessage }}
          <button class="btn-close" @click="errorMessage = ''" aria-label="Close"></button>
        </div>

        <!-- Form -->
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <form @submit.prevent="handleTransfer" novalidate>

              <!-- From account -->
              <div class="mb-3">
                <label for="fromAccount" class="form-label fw-medium">From Account</label>
                <select id="fromAccount" v-model="form.fromAccount" class="form-select" :class="{ 'is-invalid': errors.fromAccount }" required>
                  <option value="">Select source account…</option>
                  <option
                    v-for="acc in ownAccounts"
                    :key="acc.id"
                    :value="acc"
                    :disabled="!acc.active"
                  >
                    {{ capitalize(acc.accountType) }} — {{ acc.iban }} ({{ formatCurrency(acc.balance) }})
                  </option>
                </select>
                <div v-if="errors.fromAccount" class="invalid-feedback">Please select a source account.</div>
              </div>

              <!-- Own transfer: to account -->
              <div v-if="transferType === 'own'" class="mb-3">
                <label for="toOwnAccount" class="form-label fw-medium">To Account</label>
                <select id="toOwnAccount" v-model="form.toAccount" class="form-select" :class="{ 'is-invalid': errors.toAccount }" required>
                  <option value="">Select destination account…</option>
                  <option
                    v-for="acc in ownAccounts.filter(a => a !== form.fromAccount)"
                    :key="acc.id"
                    :value="acc"
                    :disabled="!acc.active"
                  >
                    {{ capitalize(acc.accountType) }} — {{ acc.iban }} ({{ formatCurrency(acc.balance) }})
                  </option>
                </select>
                <div v-if="errors.toAccount" class="invalid-feedback">Please select a destination account.</div>
              </div>

              <!-- External transfer: search IBAN -->
              <div v-if="transferType === 'external'" class="mb-3">
                <label class="form-label fw-medium">Recipient IBAN</label>
                <div class="input-group mb-2">
                  <input
                    v-model="form.toIban"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': errors.toIban }"
                    placeholder="NL00ELS0000000000"
                    aria-label="Recipient IBAN"
                  />
                  <button type="button" class="btn btn-outline-secondary" @click="toggleSearch" aria-label="Search by name">
                    🔍 Search
                  </button>
                  <div v-if="errors.toIban" class="invalid-feedback">Enter a valid recipient IBAN.</div>
                </div>

                <!-- Name search -->
                <div v-if="showSearch" class="card border mt-2 p-3">
                  <p class="small fw-medium mb-2">Search customer by name</p>
                  <div class="row g-2 mb-2">
                    <div class="col">
                      <input v-model="searchFirst" type="text" class="form-control form-control-sm" placeholder="First name" aria-label="First name" />
                    </div>
                    <div class="col">
                      <input v-model="searchLast" type="text" class="form-control form-control-sm" placeholder="Last name" aria-label="Last name" />
                    </div>
                    <div class="col-auto">
                      <button type="button" class="btn btn-primary btn-sm" :disabled="searchLoading" @click="searchCustomer">
                        <span v-if="searchLoading" class="spinner-border spinner-border-sm" aria-hidden="true"></span>
                        <span v-else>Search</span>
                      </button>
                    </div>
                  </div>
                  <div v-if="searchResults.length > 0" class="list-group list-group-flush">
                    <button
                      v-for="r in searchResults"
                      :key="r.iban"
                      type="button"
                      class="list-group-item list-group-item-action d-flex justify-content-between align-items-center small"
                      @click="selectIban(r.iban)"
                    >
                      <span>{{ r.firstName }} {{ r.lastName }}</span>
                      <span class="els-iban-text">{{ r.iban }}</span>
                    </button>
                  </div>
                  <p v-else-if="searched" class="small text-muted mb-0">No customers found.</p>
                </div>
              </div>

              <!-- Amount -->
              <div class="mb-3">
                <label for="amount" class="form-label fw-medium">Amount (EUR)</label>
                <div class="input-group">
                  <span class="input-group-text">€</span>
                  <input
                    id="amount"
                    v-model="form.amount"
                    type="number"
                    step="0.01"
                    min="0.01"
                    class="form-control"
                    :class="{ 'is-invalid': errors.amount }"
                    placeholder="0.00"
                    required
                  />
                  <div v-if="errors.amount" class="invalid-feedback">{{ errors.amount }}</div>
                </div>
              </div>

              <!-- Description -->
              <div class="mb-4">
                <label for="description" class="form-label fw-medium">Description <span class="text-muted">(optional)</span></label>
                <input
                  id="description"
                  v-model="form.description"
                  type="text"
                  class="form-control"
                  placeholder="Payment for…"
                  maxlength="255"
                />
              </div>

              <button type="submit" class="btn btn-primary w-100" :disabled="submitting">
                <span v-if="submitting" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                {{ submitting ? 'Processing…' : 'Confirm Transfer' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useUsersStore } from '../../stores/users'
import { useTransactionsStore } from '../../stores/transactions'

const auth = useAuthStore()
const usersStore = useUsersStore()
const txStore = useTransactionsStore()

const transferType = ref('own')
const submitting = ref(false)
const success = ref(false)
const errorMessage = ref('')

const form = reactive({
  fromAccount: '',
  toAccount: '',
  toIban: '',
  amount: '',
  description: '',
})
const errors = reactive({})

const showSearch = ref(false)
const searchFirst = ref('')
const searchLast = ref('')
const searchResults = ref([])
const searchLoading = ref(false)
const searched = ref(false)

const ownAccounts = computed(() => auth.currentUser?.accounts || [])

function formatCurrency(v) {
  return new Intl.NumberFormat('nl-NL', { style: 'currency', currency: 'EUR' }).format(v || 0)
}

function capitalize(s) {
  return s ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase() : ''
}

function toggleSearch() {
  showSearch.value = !showSearch.value
  searchResults.value = []
  searched.value = false
}

async function searchCustomer() {
  if (!searchFirst.value || !searchLast.value) return
  searchLoading.value = true
  searched.value = false
  try {
    searchResults.value = await usersStore.searchCustomers(searchFirst.value, searchLast.value)
    searched.value = true
  } catch {
    searchResults.value = []
    searched.value = true
  } finally {
    searchLoading.value = false
  }
}

function selectIban(iban) {
  form.toIban = iban
  showSearch.value = false
}

function validate() {
  Object.keys(errors).forEach((k) => delete errors[k])

  if (!form.fromAccount) errors.fromAccount = true

  if (transferType.value === 'own') {
    if (!form.toAccount) errors.toAccount = true
  } else {
    if (!form.toIban?.trim()) errors.toIban = true
  }

  const amt = parseFloat(form.amount)
  if (!form.amount || isNaN(amt) || amt <= 0) {
    errors.amount = 'Enter a valid positive amount.'
  }

  return Object.keys(errors).length === 0
}

async function handleTransfer() {
  success.value = false
  errorMessage.value = ''

  if (!validate()) return

  const toIban =
    transferType.value === 'own' ? form.toAccount.iban : form.toIban.trim()

  submitting.value = true
  try {
    await txStore.createTransaction({
      from_iban: form.fromAccount.iban,
      to_iban: toIban,
      initiated_by_user_id: auth.currentUser?.id,
      amount: parseFloat(form.amount),
      transaction_type: 'transfer',
      currency: 'EUR',
      description: form.description || 'Transfer',
    })
    success.value = true
    form.fromAccount = ''
    form.toAccount = ''
    form.toIban = ''
    form.amount = ''
    form.description = ''
    await auth.refreshUser()
  } catch (err) {
    errorMessage.value = err.response?.data?.message || 'Transfer failed. Please check the limits and try again.'
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await auth.refreshUser()
})
</script>
