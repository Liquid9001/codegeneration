<template>
  <nav class="navbar navbar-expand-lg navbar-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">ELS</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
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
import { useRouter } from 'vue-router';

export default {
  setup() {
    const authStore = useAuthStore();
    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const currentUser = computed(() => authStore.currentUser);
    const router = useRouter();
    const logout = () => {
      authStore.logout();
      router.push('/login');
    };

    const isEmployeeOrAdmin = computed(() => {
      return currentUser.value && (currentUser.value.role === 'EMPLOYEE' || currentUser.value.role === 'ADMIN');
    });

    return {
      isLoggedIn,
      currentUser,
      logout,
      isEmployeeOrAdmin
    };
  }
};
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 15px 0;
}

.navbar-brand {
  font-weight: 700;
  font-size: 24px;
  color: white !important;
  text-decoration: none;
}

.navbar-toggler {
  border: none;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 12px;
}

.navbar-toggler:focus {
  outline: none;
  box-shadow: none;
}

.navbar-toggler-icon {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='30' height='30' viewBox='0 0 30 30'%3e%3cpath stroke='rgba%28255, 255, 255, 0.9%29' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
}

.btn-primary {
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  padding: 10px 20px;
  margin-left: 10px;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.btn-outline-danger {
  background: transparent;
  color: white;
  border: 2px solid white;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  padding: 10px 20px;
  margin-left: 10px;
}

.btn-outline-danger:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

@media (max-width: 768px) {
  .navbar {
    padding: 10px 0;
  }
  
  .navbar-brand {
    font-size: 20px;
  }
  
  .btn-primary, .btn-outline-danger {
    padding: 8px 15px;
    font-size: 14px;
  }
}
</style>
