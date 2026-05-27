<template>
  <div class="container py-4">
    <div class="els-page-header">
      <h1 class="h4 fw-bold mb-1">🏧 ATM</h1>
      <p class="text-muted small mb-0">Deposit or withdraw cash from your account</p>
    </div>

    <div class="row justify-content-center">
      <div class="col-lg-6 col-md-8">
        <!-- ATM terminal card -->
        <div class="card shadow border-0" style="border-radius: 1rem; overflow: hidden;">
          <!-- ATM Header -->
          <div class="card-header bg-dark text-white text-center py-4">
            <h2 class="h5 mb-0 fw-bold text-white">ELS Bank ATM</h2>
            <p class="small opacity-75 mb-0">Secure Self-Service Terminal</p>
          </div>

          <div class="card-body p-4">
            <!-- Success -->
            <div v-if="success" class="alert alert-success alert-dismissible" role="status">
              <strong>{{ lastAction === 'deposit' ? 'Deposit' : 'Withdrawal' }} successful!</strong>
              {{ formatCurrency(lastAmount) }} has been {{ lastAction === 'deposit' ? 'added to' : 'withdrawn from' }} your account.
              <button class="btn-close" @click="success = false" aria-label="Close"></button>
            </div>

            <!-- Error -->
            <div v-if="errorMessage" class="alert alert-danger alert-dismissible" role="alert">
              {{ errorMessage }}
              <button class="btn-close" @click="errorMessage = ''" aria-label="Close"></button>
            </div>

            <!-- Account selection -->
            <div class="mb-4">
              <label for="atmAccount" class="form-label fw-medium">Select Account</label>
              <select id="atmAccount" v-model="selectedAccount" class="form-select" :class="{ 'is-invalid': errors.account }">
                <option value="">Choose account…</option>
                <option
                  v-for="acc in checkingAccounts"
                  :key="acc.id"
                  :value="acc"
                  :disabled="!acc.active"
                >
                  {{ capitalize(acc.accountType) }} — {{ acc.iban }} ({{ formatCurrency(acc.balance) }})
                </option>
              </select>
              <div v-if="errors.account" class="invalid-feedback">Please select an account.</div>
            </div>

            <!-- Balance display -->
            <div v-if="selectedAccount" class="bg-dark text-white rounded-3 p-3 mb-4 text-center" aria-live="polite">
              <p class="small opacity-75 mb-1">Available Balance</p>
              <div class="fs-3 fw-bold">{{ formatCurrency(selectedAccount.balance) }}</div>
              <p class="small opacity-50 mb-0">{{ selectedAccount.iban }}</p>
            </div>

            <!-- Operation type -->
            <div class="mb-3">
              <label class="form-label fw-medium">Operation</label>
              <div class="btn-group w-100" role="group" aria-label="ATM operation">
                <input type="radio" class="btn-check" id="opDeposit" value="deposit" v-model="operation" />
                <label class="btn btn-outline-success" for="opDeposit">
                  ↓ Deposit
                </label>
                <input type="radio" class="btn-check" id="opWithdraw" value="withdrawal" v-model="operation" />
                <label class="btn btn-outline-danger" for="opWithdraw">
                  ↑ Withdraw
                </label>
              </div>
            </div>

            <!-- Amount -->
            <div class="mb-4">
              <label for="atmAmount" class="form-label fw-medium">Amount (EUR)</label>
              <div class="input-group input-group-lg">
                <span class="input-group-text">€</span>
                <input
                  id="atmAmount"
                  v-model="amount"
                  type="number"
                  step="10"
                  min="10"
                  class="form-control"
                  :class="{ 'is-invalid': errors.amount }"
                  placeholder="0.00"
                  aria-label="Amount in euros"
                />
                <div v-if="errors.amount" class="invalid-feedback">{{ errors.amount }}</div>
              </div>

              <!-- Quick amounts -->
              <div class="d-flex gap-2 mt-2 flex-wrap">
                <button
                  v-for="quick in quickAmounts"
                  :key="quick"
                  type="button"
                  class="btn btn-outline-secondary btn-sm"
                  @click="amount = quick"
                  :aria-label="`Select ${formatCurrency(quick)}`"
                >
                  {{ formatCurrency(quick) }}
                </button>
              </div>
            </div>

            <!-- Confirm button -->
            <button
              class="btn w-100 btn-lg fw-semibold"
              :class="operation === 'deposit' ? 'btn-success' : 'btn-danger'"
              :disabled="submitting"
              @click="handleAtm"
            >
              <span v-if="submitting" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
              <span v-else>
                {{ operation === 'deposit' ? '↓ Deposit' : '↑ Withdraw' }}
                {{ amount ? formatCurrency(parseFloat(amount)) : '' }}
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useTransactionsStore } from '../../stores/transactions'

const ATM_IBAN = 'NL00ELSA0000000000'

const auth = useAuthStore()
const txStore = useTransactionsStore()

const selectedAccount = ref('')
const operation = ref('deposit')
const amount = ref('')
const submitting = ref(false)
const success = ref(false)
const errorMessage = ref('')
const lastAction = ref('')
const lastAmount = ref(0)
const errors = reactive({})

const quickAmounts = [20, 50, 100, 200, 500]

const checkingAccounts = computed(() =>
  (auth.currentUser?.accounts || []).filter((a) => a.active)
)

function formatCurrency(v) {
  return new Intl.NumberFormat('nl-NL', { style: 'currency', currency: 'EUR' }).format(v || 0)
}

function capitalize(s) {
  return s ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase() : ''
}

function validate() {
  Object.keys(errors).forEach((k) => delete errors[k])
  if (!selectedAccount.value) errors.account = true
  const amt = parseFloat(amount.value)
  if (!amount.value || isNaN(amt) || amt < 10) {
    errors.amount = 'Minimum amount is €10.00.'
  } else if (operation.value === 'withdrawal' && amt > (selectedAccount.value?.balance || 0)) {
    errors.amount = 'Insufficient balance.'
  }
  return Object.keys(errors).length === 0
}

async function handleAtm() {
  success.value = false
  errorMessage.value = ''
  if (!validate()) return

  const amt = parseFloat(amount.value)
  submitting.value = true

  try {
    const payload = {
      from_iban: operation.value === 'withdrawal' ? selectedAccount.value.iban : ATM_IBAN,
      to_iban: operation.value === 'deposit' ? selectedAccount.value.iban : ATM_IBAN,
      initiated_by_user_id: auth.currentUser?.id,
      amount: amt,
      transaction_type: operation.value,
      currency: 'EUR',
      description: `ATM ${operation.value}`,
    }

    await txStore.createTransaction(payload)
    lastAction.value = operation.value
    lastAmount.value = amt
    success.value = true
    amount.value = ''
    await auth.refreshUser()
  } catch (err) {
    errorMessage.value = err.response?.data?.message || `${capitalize(operation.value)} failed. Please try again.`
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await auth.refreshUser()
})
</script>
