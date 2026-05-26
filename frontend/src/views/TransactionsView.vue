<template>
  <div>
    <h1 class="mb-4">
      Transactions
    </h1>

    <div v-if="loading" class="alert alert-info">
      Loading transactions...
    </div>

    <div class="card shadow-sm">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th>ID</th>
                <th>From</th>
                <th>To</th>
                <th>Amount</th>
                <th>Type</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="transaction in transactions"
                :key="transaction.id"
              >
                <td>{{ transaction.id }}</td>
                <td>{{ transaction.from_iban }}</td>
                <td>{{ transaction.to_iban }}</td>
                <td>
                  € {{ Number(transaction.amount).toFixed(2) }}
                </td>
                <td>{{ transaction.transaction_type }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getTransactions } from '../api/transactionApi'

const transactions = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true

  try {
    transactions.value = await getTransactions()
  } catch (e) {
    console.error(e)
  }

  loading.value = false
})
</script>