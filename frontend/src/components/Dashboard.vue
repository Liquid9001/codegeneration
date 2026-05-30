<template>
  <div class="dashboard">
    <h1>{{ dashboardTitle }}</h1>
    <div v-if="user.accounts && user.accounts.length > 0 && !isAdminOrEmployee">
      <BankAccount v-for="account in user.accounts" :key="account.id" :account="account" />
    </div>
    <p v-else-if="!isAdminOrEmployee && !user.approved">Een medewerker moet u eerst goedkeuren om een bankrekening te kunnen hebben.</p>
  </div>
</template>

<script>
import { useAuthStore } from '../store/auth';
import BankAccount from './BankAccount.vue';

export default {
  name: 'Dashboard',
  components: {
    BankAccount
  },
  data() {
    return {
      user: {}
    };
  },
  computed: {
    dashboardTitle() {
      const authStore = useAuthStore();
      if (authStore.isLoggedIn && (authStore.user.role === 'ADMIN' || authStore.user.role === 'EMPLOYEE')) {
        return 'Admin Dashboard';
      }
      return 'Dashboard';
    },
    isAdminOrEmployee() {
      const authStore = useAuthStore();
      return authStore.isLoggedIn && (authStore.user.role === 'ADMIN' || authStore.user.role === 'EMPLOYEE');
    }
  },
  async created() {
    const authStore = useAuthStore();
    if (authStore.isLoggedIn) {
      this.user = authStore.user;
    }
  }
};
</script>

<style scoped>
.dashboard {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
</style>
