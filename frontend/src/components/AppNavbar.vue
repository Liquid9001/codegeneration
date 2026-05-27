<template>
  <nav class="navbar navbar-expand-lg els-navbar" aria-label="Main navigation">
    <div class="container-fluid px-4">
      <!-- Brand -->
      <router-link class="navbar-brand d-flex align-items-center gap-2" to="/">
        <svg width="28" height="28" viewBox="0 0 28 28" fill="none" aria-hidden="true">
          <rect width="28" height="28" rx="6" fill="rgba(255,255,255,0.2)" />
          <path d="M4 18 L14 8 L24 18" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
          <rect x="8" y="18" width="4" height="5" rx="1" fill="white" />
          <rect x="16" y="18" width="4" height="5" rx="1" fill="white" />
          <rect x="6" y="17" width="16" height="2" rx="1" fill="white" />
        </svg>
        <span class="els-brand-text">ELS Bank</span>
      </router-link>

      <!-- Toggler -->
      <button
        class="navbar-toggler border-0"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#mainNavbar"
        aria-controls="mainNavbar"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- Links -->
      <div class="collapse navbar-collapse" id="mainNavbar">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <!-- Customer links -->
          <template v-if="auth.isAuthenticated && !auth.isEmployee && auth.isApproved">
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/dashboard">
                Dashboard
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/transfer">
                Transfer
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/transactions">
                Transactions
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/atm">
                ATM
              </router-link>
            </li>
          </template>

          <!-- Employee links -->
          <template v-if="auth.isAuthenticated && auth.isEmployee">
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/employee">
                Dashboard
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/employee/customers">
                Customers
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/employee/customers/pending">
                Pending
                <span v-if="pendingCount > 0" class="badge bg-warning text-dark ms-1">{{ pendingCount }}</span>
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active fw-semibold" to="/employee/transactions">
                Transactions
              </router-link>
            </li>
          </template>
        </ul>

        <!-- Right side -->
        <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center gap-2">
          <template v-if="auth.isAuthenticated">
            <li class="nav-item">
              <span class="nav-link text-white-50 small">
                {{ userDisplayName }}
                <span v-if="auth.isEmployee" class="badge bg-warning text-dark ms-1 small">Employee</span>
              </span>
            </li>
            <li class="nav-item">
              <button class="btn btn-outline-light btn-sm" @click="handleLogout">Logout</button>
            </li>
          </template>
          <template v-else>
            <li class="nav-item">
              <router-link class="btn btn-outline-light btn-sm" to="/login">Login</router-link>
            </li>
            <li class="nav-item">
              <router-link class="btn btn-light btn-sm" to="/register">Register</router-link>
            </li>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useUsersStore } from '../stores/users'

const auth = useAuthStore()
const users = useUsersStore()
const router = useRouter()

const pendingCount = ref(0)

const userDisplayName = computed(() => {
  const u = auth.currentUser
  if (!u) return ''
  if (u.firstName && u.lastName) return `${u.firstName} ${u.lastName}`
  return u.email || ''
})

async function handleLogout() {
  auth.logout()
  router.push('/login')
}

onMounted(async () => {
  if (auth.isEmployee) {
    try {
      const pending = await users.getPendingUsers()
      pendingCount.value = pending.length
    } catch {
      // ignore
    }
  }
})
</script>
