<template>
  <nav class="navbar">
    <div class="container-fluid nav-content">
      <router-link class="navbar-brand" to="/">ELS</router-link>
      <div class="nav-actions">
        <template v-if="isLoggedIn">
          <router-link to="/" class="btn btn-outline-light nav-btn">Dashboard</router-link>
          <router-link v-if="isCustomer" to="/transfer" class="btn btn-outline-light nav-btn">Overboeken</router-link>
          <router-link to="/transactions" class="btn btn-outline-light nav-btn">Transacties</router-link>
          <router-link v-if="isEmployeeOrAdmin" to="/admin/users" class="btn btn-outline-light nav-btn">Gebruikers</router-link>
          <button @click="logout" class="btn btn-outline-danger nav-btn">Logout</button>
        </template>

        <template v-else>
          <router-link to="/login" class="btn btn-primary nav-btn">Login</router-link>
          <router-link to="/register" class="btn btn-secondary nav-btn">Register</router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const currentUser = computed(() => authStore.currentUser);
    const isEmployeeOrAdmin = computed(() => (
      currentUser.value?.role === 'EMPLOYEE' || currentUser.value?.role === 'ADMIN'
    ));
    const isCustomer = computed(() => currentUser.value?.role === 'CUSTOMER');

    const logout = () => {
      authStore.logout();
      router.push('/login');
    };

    return {
      isLoggedIn,
      isEmployeeOrAdmin,
      isCustomer,
      logout,
    };
  },
};
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 15px 0;
}

.nav-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.navbar-brand {
  font-weight: 700;
  font-size: 24px;
  color: white !important;
  text-decoration: none;
  cursor: pointer;
}

.nav-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.nav-btn {
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  letter-spacing: 0.3px;
  transition: all 0.2s ease;
  padding: 9px 16px;
}

.btn-primary {
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  background: linear-gradient(to right, #ff7e5f 0%, #feb47b 100%);
  color: white;
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.btn-outline-light,
.btn-outline-danger {
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.85);
}

.btn-outline-light:hover,
.btn-outline-danger:hover,
.router-link-active.btn-outline-light {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .navbar {
    padding: 10px 0;
  }

  .navbar-brand {
    font-size: 20px;
  }

  .nav-actions {
    justify-content: flex-start;
    width: 100%;
  }

  .nav-btn {
    padding: 8px 12px;
    font-size: 14px;
  }
}
</style>
