<template>
  <div class="container py-4">
    <div class="els-page-header">
      <h1 class="h4 fw-bold mb-1">Employee Dashboard</h1>
      <p class="text-muted small mb-0">Welcome, {{ employeeName }} — ELS Bank Operations</p>
    </div>

    <AppSpinner v-if="loading" />

    <template v-else>
      <!-- Stats row -->
      <div class="row g-3 mb-4">
        <div class="col-sm-6 col-lg-3">
          <div class="card els-stat-card shadow-sm h-100">
            <div class="card-body">
              <p class="text-muted small mb-1">Total Customers</p>
              <div class="fs-3 fw-bold">{{ stats.totalCustomers }}</div>
              <router-link to="/employee/customers" class="stretched-link small text-decoration-none">View all →</router-link>
            </div>
          </div>
        </div>
        <div class="col-sm-6 col-lg-3">
          <div class="card shadow-sm h-100 border-start border-warning border-4">
            <div class="card-body">
              <p class="text-muted small mb-1">Pending Approvals</p>
              <div class="fs-3 fw-bold text-warning">{{ stats.pendingApprovals }}</div>
              <router-link to="/employee/customers/pending" class="stretched-link small text-decoration-none">Review →</router-link>
            </div>
          </div>
        </div>
        <div class="col-sm-6 col-lg-3">
          <div class="card shadow-sm h-100 border-start border-success border-4">
            <div class="card-body">
              <p class="text-muted small mb-1">Active Customers</p>
              <div class="fs-3 fw-bold text-success">{{ stats.activeCustomers }}</div>
              <router-link to="/employee/customers" class="stretched-link small text-decoration-none">View →</router-link>
            </div>
          </div>
        </div>
        <div class="col-sm-6 col-lg-3">
          <div class="card shadow-sm h-100 border-start border-info border-4">
            <div class="card-body">
              <p class="text-muted small mb-1">Total Transactions</p>
              <div class="fs-3 fw-bold text-info">{{ stats.totalTransactions }}</div>
              <router-link to="/employee/transactions" class="stretched-link small text-decoration-none">View →</router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick actions -->
      <h2 class="h6 fw-semibold text-muted text-uppercase mb-3">Quick Actions</h2>
      <div class="row g-3 mb-4">
        <div class="col-6 col-md-3">
          <router-link to="/employee/customers/pending" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">✅</div>
              <div class="fw-medium small">Approve Customers</div>
            </div>
          </router-link>
        </div>
        <div class="col-6 col-md-3">
          <router-link to="/employee/customers" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">👥</div>
              <div class="fw-medium small">All Customers</div>
            </div>
          </router-link>
        </div>
        <div class="col-6 col-md-3">
          <router-link to="/employee/transactions" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">📊</div>
              <div class="fw-medium small">All Transactions</div>
            </div>
          </router-link>
        </div>
        <div class="col-6 col-md-3">
          <router-link to="/employee/customers" class="card text-decoration-none border els-stat-card h-100">
            <div class="card-body text-center py-4">
              <div class="fs-2 mb-2">⚙️</div>
              <div class="fw-medium small">Manage Limits</div>
            </div>
          </router-link>
        </div>
      </div>

      <!-- Recent pending approvals -->
      <div v-if="pendingCustomers.length > 0" class="card shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
          <h2 class="h6 fw-semibold mb-0 d-flex align-items-center gap-2">
            Pending Approvals
            <span class="badge bg-warning text-dark">{{ pendingCustomers.length }}</span>
          </h2>
          <router-link to="/employee/customers/pending" class="btn btn-warning btn-sm">
            Review All
          </router-link>
        </div>
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Registered</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in pendingCustomers.slice(0, 5)" :key="user.id">
                <td>{{ user.firstName }} {{ user.lastName }}</td>
                <td class="small text-muted">{{ user.email }}</td>
                <td class="small text-muted">{{ formatDate(user.createdAt) }}</td>
                <td class="text-end">
                  <router-link :to="`/employee/customers/${user.id}`" class="btn btn-sm btn-outline-primary">View</router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useUsersStore } from '../../stores/users'
import { useTransactionsStore } from '../../stores/transactions'
import AppSpinner from '../../components/AppSpinner.vue'

const auth = useAuthStore()
const usersStore = useUsersStore()
const txStore = useTransactionsStore()

const loading = ref(false)
const allUsers = ref([])
const pendingCustomers = ref([])
const transactionCount = ref(0)

const employeeName = computed(() => {
  const u = auth.currentUser
  if (!u) return ''
  if (u.firstName) return u.firstName
  return u.email?.split('@')[0] || 'Employee'
})

const stats = computed(() => {
  const customers = allUsers.value.filter((u) => u.role === 'CUSTOMER')
  return {
    totalCustomers: customers.length,
    pendingApprovals: pendingCustomers.value.length,
    activeCustomers: customers.filter((u) => u.approved).length,
    totalTransactions: transactionCount.value,
  }
})

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('nl-NL', { day: '2-digit', month: 'short', year: 'numeric' })
}

onMounted(async () => {
  loading.value = true
  try {
    const [users] = await Promise.all([
      usersStore.getAllUsers(0, 200),
    ])
    allUsers.value = users
    pendingCustomers.value = users.filter((u) => u.role === 'CUSTOMER' && !u.approved)

    try {
      const txs = await txStore.getAllTransactions({ offset: 0, limit: 1 })
      transactionCount.value = txs.length
    } catch {
      transactionCount.value = 0
    }
  } finally {
    loading.value = false
  }
})
</script>
