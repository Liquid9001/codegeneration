<template>
  <div class="dashboard">
    <h1>Dashboard</h1>
    <div v-if="user.accounts && user.accounts.length > 0">
      <BankAccount v-for="account in user.accounts" :key="account.id" :account="account" />
    </div>
    <p v-else>Geen accounts gevonden</p>
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
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
</style>
