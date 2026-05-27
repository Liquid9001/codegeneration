<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand fw-bold" to="/dashboard">
        ELS Bank
      </router-link>

      <div class="navbar-nav ms-auto align-items-center">
        <router-link class="nav-link" to="/dashboard">
          Dashboard
        </router-link>

        <router-link class="nav-link" to="/transactions">
          Transactions
        </router-link>

        <router-link class="nav-link" to="/transfer">
          Transfer
        </router-link>

        <router-link
          v-if="authStore.isEmployee"
          class="nav-link"
          to="/employee"
        >
          Employee
        </router-link>

        <button
          class="btn btn-outline-light ms-3"
          @click="logout"
        >
          Logout
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>