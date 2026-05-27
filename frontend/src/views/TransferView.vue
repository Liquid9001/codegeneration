<template>
  <div class="row justify-content-center">
    <div class="col-lg-7">
      <div class="card shadow">
        <div class="card-body p-4">
          <h2 class="mb-4">
            Transfer Funds
          </h2>

          <form @submit.prevent="submit">

            <div class="mb-3">
              <label class="form-label">From IBAN</label>

              <input
                v-model="form.from_iban"
                class="form-control"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label">To IBAN</label>

              <input
                v-model="form.to_iban"
                class="form-control"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label">Amount</label>

              <input
                v-model="form.amount"
                type="number"
                class="form-control"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label">Description</label>

              <textarea
                v-model="form.description"
                class="form-control"
              ></textarea>
            </div>

            <button class="btn btn-primary">
              Send Transfer
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { createTransaction } from '../api/transactionApi'
import { useAuthStore } from '../stores/authStore'

const authStore = useAuthStore()

const form = reactive({
  from_iban: '',
  to_iban: '',
  amount: 0,
  description: '',
  transaction_type: 'transfer',
  currency: 'EUR'
})

const submit = async () => {
  await createTransaction({
    ...form,
    initiated_by_user_id:
      authStore.user?.sub || authStore.user?.id
  })

  alert('Transfer successful')
}
</script>