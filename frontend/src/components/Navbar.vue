<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">ELS</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <!-- Removed links for accounts, transactions, and customers -->
        </ul>
        <div v-if="isLoggedIn" class="d-flex align-items-center">
          <button @click="logout" class="btn btn-outline-danger">Logout</button>
        </div>
        <div v-else>
          <router-link to="/login" class="btn btn-primary">Login</router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { useAuthStore } from '../store/auth';
import { computed } from 'vue';

export default {
  setup() {
    const authStore = useAuthStore();
    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const currentUser = computed(() => authStore.currentUser);

    const logout = () => {
      authStore.logout();
      this.$router.push('/login');
    };

    return {
      isLoggedIn,
      currentUser,
      logout
    };
  }
};
</script>

<style scoped>
.navbar {
  background-color: #f8f9fa;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0056b3;
  border-color: #004085;
}
</style>
