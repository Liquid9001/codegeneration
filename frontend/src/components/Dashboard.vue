<template>
  <div class="dashboard">
    <h1 class="dashboard-title">{{ dashboardTitle }}</h1>
    <div v-if="user.accounts && user.accounts.length > 0 && !isAdminOrEmployee" class="accounts-container">
      <BankAccount v-for="account in user.accounts" :key="account.id" :account="account" />
    </div>
    <div v-else-if="!isAdminOrEmployee && !user.approved" class="approval-message">
      <p>Een medewerker moet u eerst goedkeuren om een bankrekening te kunnen hebben.</p>
    </div>
    <div v-else-if="isAdminOrEmployee" class="admin-content">
      <p>Dit is het admin dashboard. Hier kunt u alle gebruikers en rekeningen beheren.</p>
      <router-link to="/admin/users" class="btn btn-primary">Gebruikers beheren</router-link>
    </div>
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
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.dashboard-title {
  text-align: center;
  margin-bottom: 30px;
  font-weight: 600;
  font-size: 28px;
  letter-spacing: 0.5px;
}

.accounts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  max-width: 800px;
}

.approval-message {
  text-align: center;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  margin-top: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 800px;
}

.approval-message p {
  margin: 0;
  font-size: 16px;
}

.admin-content {
  text-align: center;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  margin-top: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 800px;
}

.admin-content p {
  margin: 0;
  font-size: 16px;
}

.admin-content .btn {
  margin-top: 20px;
  padding: 10px 20px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.admin-content .btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

@media (max-width: 768px) {
  .dashboard {
    padding: 20px 10px;
  }
  
  .dashboard-title {
    font-size: 24px;
  }
}
</style>
