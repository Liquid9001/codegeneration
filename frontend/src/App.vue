<template>
  <div class="bg-light min-vh-100">
    <AppNavbar v-if="authStore.isAuthenticated" />

    <div class="container py-4">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import AppNavbar from './components/AppNavbar.vue'
import { useAuthStore } from './stores/authStore'

const authStore = useAuthStore()
</script>