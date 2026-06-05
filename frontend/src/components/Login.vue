<template>
  <div class="login-container">
    <div class="login-wrapper">
      <h2 class="login-title">Welkom terug</h2>
      <form @submit.prevent="login" class="login-form">
        <div class="form-group">
          <label for="email" class="form-label">Emailadres</label>
          <input type="email" v-model="email" id="email" class="form-control" required />
        </div>
        <div class="form-group">
          <label for="password" class="form-label">Wachtwoord</label>
          <input type="password" v-model="password" id="password" class="form-control" required />
        </div>
        <span v-if="errorMessage" class="error">{{ errorMessage }}</span>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Inloggen...' : 'Inloggen' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../store/auth';
import { loginUser } from '../services/authService';
import { getErrorMessage } from '../services/errorUtils';

export default {
  name: 'Login',
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
      loading: false,
    };
  },
  methods: {
    async login() {
      this.errorMessage = '';
      this.loading = true;

      try {
        const { token, user } = await loginUser(this.email, this.password);
        useAuthStore().login(token, user);
        this.$router.push('/');
      } catch (error) {
        console.error('Login failed:', error);
        this.errorMessage = getErrorMessage(error, 'Invalid credentials');
      } finally {
        this.loading = false;
      }
    },
    isAuthorized(role) {
      return role === 'ADMIN' || role === 'EMPLOYEE';
    }
  },
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-weight: 600;
  font-size: 28px;
  letter-spacing: 0.5px;
  color: white;
}

.login-form {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 16px;
  color: white;
}

.form-control {
  width: 100%;
  padding: 14px;
  box-sizing: border-box;
  border: none;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.form-control:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.error {
  color: #ffdddd;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

.btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.btn:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .login-form {
    padding: 20px;
  }
  
  .form-control {
    padding: 12px;
  }
  
  .btn {
    padding: 12px;
  }
}
</style>
