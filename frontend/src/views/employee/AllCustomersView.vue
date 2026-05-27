<template>
  <div class="container py-4">
    <div class="els-page-header d-flex align-items-start justify-content-between flex-wrap gap-2">
      <div>
        <h1 class="h4 fw-bold mb-1">All Customers</h1>
        <p class="text-muted small mb-0">Manage customer accounts and limits</p>
      </div>
    </div>

    <!-- Search -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-2 align-items-end">
          <div class="col-md-4">
            <label for="searchQuery" class="form-label small fw-medium">Search by name or email</label>
            <input
              id="searchQuery"
              v-model="searchQuery"
              type="search"
              class="form-control form-control-sm"
              placeholder="Search…"
              aria-label="Search customers"
            />
          </div>
          <div class="col-md-3">
            <label for="filterStatus" class="form-label small fw-medium">Status</label>
            <select id="filterStatus" v-model="filterStatus" class="form-select form-select-sm">
              <option value="">All</option>
              <option value="approved">Approved</option>
              <option value="pending">Pending Approval</option>
            </select>
          </div>
          <div class="col-auto">
            <button class="btn btn-outline-secondary btn-sm" @click="resetFilters">Reset</button>
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow-sm">
      <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
        <h2 class="h6 fw-semibold mb-0">
          Customers
          <span class="text-muted fw-normal small">({{ filteredUsers.length }})</span>
        </h2>
      </div>

      <div class="card-body p-0">
        <AppSpinner v-if="loading" />

        <div v-else-if="filteredUsers.length === 0" class="els-empty-state">
          <p class="mb-0">No customers found.</p>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th scope="col">Name</th>
                <th scope="col">Email</th>
                <th scope="col">BSN</th>
                <th scope="col">Accounts</th>
                <th scope="col">Status</th>
                <th scope="col">Registered</th>
                <th scope="col"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in pagedUsers" :key="user.id">
                <td class="fw-medium">{{ user.firstName }} {{ user.lastName }}</td>
                <td class="small text-muted">{{ user.email }}</td>
                <td class="small">{{ user.bsn || '—' }}</td>
                <td>
                  <span v-if="user.accounts?.length > 0" class="badge bg-success-subtle text-success-emphasis">
                    {{ user.accounts.length }} account{{ user.accounts.length !== 1 ? 's' : '' }}
                  </span>
                  <span v-else class="badge bg-secondary-subtle text-secondary">No accounts</span>
                </td>
                <td>
                  <span class="badge" :class="user.approved ? 'bg-success' : 'bg-warning text-dark'">
                    {{ user.approved ? 'Approved' : 'Pending' }}
                  </span>
                </td>
                <td class="small text-muted">{{ formatDate(user.createdAt) }}</td>
                <td class="text-end">
                  <router-link :to="`/employee/customers/${user.id}`" class="btn btn-sm btn-outline-primary">
                    Manage
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="filteredUsers.length > pageSize" class="card-footer bg-white d-flex justify-content-between align-items-center py-3">
        <span class="small text-muted">
          Showing {{ (currentPage - 1) * pageSize + 1 }}–{{ Math.min(currentPage * pageSize, filteredUsers.length) }}
          of {{ filteredUsers.length }}
        </span>
        <AppPagination v-model="currentPage" :total-items="filteredUsers.length" :page-size="pageSize" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUsersStore } from '../../stores/users'
import AppSpinner from '../../components/AppSpinner.vue'
import AppPagination from '../../components/AppPagination.vue'

const usersStore = useUsersStore()

const loading = ref(false)
const allUsers = ref([])
const searchQuery = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = 20

const filteredUsers = computed(() => {
  let list = allUsers.value.filter((u) => u.role === 'CUSTOMER' || u.role === 'customer' || !u.role || u.role === null)

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(
      (u) =>
        `${u.firstName} ${u.lastName}`.toLowerCase().includes(q) ||
        u.email?.toLowerCase().includes(q)
    )
  }

  if (filterStatus.value === 'approved') list = list.filter((u) => u.approved)
  else if (filterStatus.value === 'pending') list = list.filter((u) => !u.approved)

  return list
})

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('nl-NL', { day: '2-digit', month: 'short', year: 'numeric' })
}

function resetFilters() {
  searchQuery.value = ''
  filterStatus.value = ''
  currentPage.value = 1
}

onMounted(async () => {
  loading.value = true
  try {
    allUsers.value = await usersStore.getAllUsers(0, 500)
  } finally {
    loading.value = false
  }
})
</script>
