<template>
  <div class="container py-4">
    <div class="els-page-header">
      <h1 class="h4 fw-bold mb-1">Pending Customer Approvals</h1>
      <p class="text-muted small mb-0">Review and approve new customer registrations</p>
    </div>

    <AppSpinner v-if="loading" />

    <template v-else>
      <div v-if="pendingUsers.length === 0" class="els-empty-state card shadow-sm py-5">
        <div class="fs-1 mb-2">✅</div>
        <p class="fw-medium mb-1">All caught up!</p>
        <p class="text-muted small mb-0">No customers are waiting for approval.</p>
      </div>

      <div v-else class="card shadow-sm">
        <div class="card-header bg-white d-flex align-items-center justify-content-between py-3">
          <h2 class="h6 fw-semibold mb-0 d-flex align-items-center gap-2">
            Pending Registrations
            <span class="badge bg-warning text-dark">{{ pendingUsers.length }}</span>
          </h2>
        </div>
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th scope="col">Name</th>
                  <th scope="col">Email</th>
                  <th scope="col">Phone</th>
                  <th scope="col">BSN</th>
                  <th scope="col">Registered</th>
                  <th scope="col"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in pendingUsers" :key="user.id">
                  <td class="fw-medium">{{ user.firstName }} {{ user.lastName }}</td>
                  <td class="small text-muted">{{ user.email }}</td>
                  <td class="small">{{ user.phoneNumber || '—' }}</td>
                  <td class="small">{{ user.bsn || '—' }}</td>
                  <td class="small text-muted">{{ formatDate(user.createdAt) }}</td>
                  <td class="text-end d-flex gap-2 justify-content-end">
                    <router-link :to="`/employee/customers/${user.id}`" class="btn btn-sm btn-outline-secondary">
                      View
                    </router-link>
                    <button
                      class="btn btn-sm btn-success"
                      :disabled="approving === user.id"
                      @click="handleApprove(user)"
                    >
                      <span v-if="approving === user.id" class="spinner-border spinner-border-sm me-1" aria-hidden="true"></span>
                      {{ approving === user.id ? 'Approving…' : '✓ Approve' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

    <!-- Approval modal: set initial limits -->
    <div class="modal fade" id="approveModal" tabindex="-1" aria-labelledby="approveModalLabel" aria-modal="true" ref="approveModalEl">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h3 class="modal-title h6 fw-bold" id="approveModalLabel">
              Approve {{ selectedUser?.firstName }} {{ selectedUser?.lastName }}
            </h3>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="text-muted small mb-3">
              Set initial transfer limits for the customer's accounts. Both a checking and savings
              account will be created automatically.
            </p>
            <div class="mb-3">
              <label for="absLimit" class="form-label fw-medium">Absolute Transfer Limit (€)</label>
              <div class="input-group">
                <span class="input-group-text">€</span>
                <input id="absLimit" v-model="limitForm.absoluteLimit" type="number" min="0" step="100" class="form-control" placeholder="1000.00" />
              </div>
              <div class="form-text">The account balance will never go below this amount.</div>
            </div>
            <div class="mb-3">
              <label for="dailyLimit" class="form-label fw-medium">Daily Transfer Limit (€)</label>
              <div class="input-group">
                <span class="input-group-text">€</span>
                <input id="dailyLimit" v-model="limitForm.dailyLimit" type="number" min="0" step="100" class="form-control" placeholder="5000.00" />
              </div>
              <div class="form-text">Maximum total transfers per day.</div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-success" :disabled="approving !== null" @click="confirmApprove">
              <span v-if="approving !== null" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
              Approve & Create Accounts
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as bootstrap from 'bootstrap'
import { useUsersStore } from '../../stores/users'
import AppSpinner from '../../components/AppSpinner.vue'

const usersStore = useUsersStore()

const loading = ref(false)
const pendingUsers = ref([])
const approving = ref(null)
const selectedUser = ref(null)
const approveModalEl = ref(null)
let approveModal = null

const limitForm = reactive({
  absoluteLimit: 1000,
  dailyLimit: 5000,
})

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('nl-NL', { day: '2-digit', month: 'short', year: 'numeric' })
}

function handleApprove(user) {
  selectedUser.value = user
  approveModal?.show()
}

async function confirmApprove() {
  if (!selectedUser.value) return
  approving.value = selectedUser.value.id
  try {
    await usersStore.approveUser(selectedUser.value.id)
    pendingUsers.value = pendingUsers.value.filter((u) => u.id !== selectedUser.value.id)
    approveModal?.hide()
  } finally {
    approving.value = null
  }
}

onMounted(async () => {
  loading.value = true
  try {
    pendingUsers.value = await usersStore.getPendingUsers()
  } finally {
    loading.value = false
  }

  approveModal = new bootstrap.Modal(approveModalEl.value)
})
</script>
