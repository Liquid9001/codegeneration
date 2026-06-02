<template>
  <div class="register-container">
    <div class="register-wrapper">
      <h2 class="register-title">Registreren</h2>
      <form @submit.prevent="register" class="register-form">
        <div class="form-group">
          <label for="email" class="form-label">Emailadres</label>
          <input type="email" v-model="email" id="email" class="form-control" required />
          <span v-if="errors.email" class="error">{{ errors.email }}</span>
        </div>
        <div class="form-group">
          <label for="password" class="form-label">Wachtwoord</label>
          <input type="password" v-model="password" id="password" class="form-control" required />
          <span v-if="errors.password" class="error">{{ errors.password }}</span>
        </div>
        <div class="form-group">
          <label for="firstName" class="form-label">Voornaam</label>
          <input type="text" v-model="firstName" id="firstName" class="form-control" required />
          <span v-if="errors.firstName" class="error">{{ errors.firstName }}</span>
        </div>
        <div class="form-group">
          <label for="lastName" class="form-label">Achternaam</label>
          <input type="text" v-model="lastName" id="lastName" class="form-control" required />
          <span v-if="errors.lastName" class="error">{{ errors.lastName }}</span>
        </div>
        <div class="form-group">
          <label for="phoneNumber" class="form-label">Telefoonnummer</label>
          <input type="tel" v-model="phoneNumber" id="phoneNumber" class="form-control" required />
          <span v-if="errors.phoneNumber" class="error">{{ errors.phoneNumber }}</span>
        </div>
        <div class="form-group">
          <label for="bsn" class="form-label">BSN</label>
          <input type="text" v-model="bsn" id="bsn" class="form-control" required />
          <span v-if="errors.bsn" class="error">{{ errors.bsn }}</span>
        </div>
        <span v-if="errors.global" class="error">
          {{ errors.global }}
        </span>
        <button type="submit" class="btn btn-primary">Registreren</button>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'Register',
  data() {
    return {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phoneNumber: '',
      bsn: '',
      errors: {},
    };
  },
  methods: {
    async register() {
      this.errors = {};
      if (!this.validateEmail(this.email)) {
        this.errors.email = 'Ongeldig emailadres.';
        return;
      }
      if (this.password.length < 6) {
        this.errors.password = 'Wachtwoord moet minimaal 6 tekens lang zijn.';
        return;
      }
      if (this.firstName.trim() === '') {
        this.errors.firstName = 'Voornaam is verplicht.';
        return;
      }
      if (this.lastName.trim() === '') {
        this.errors.lastName = 'Achternaam is verplicht.';
        return;
      }
      if (!this.validatePhoneNumber(this.phoneNumber)) {
        this.errors.phoneNumber = 'Ongeldig telefoonnummer.';
        return;
      }
      if (this.bsn.length !== 9 || !/^\d+$/.test(this.bsn)) {
        this.errors.bsn = 'BSN moet 9 cijfers lang zijn.';
        return;
      }

      try {
        const response = await api.post('/users', {
          email: this.email,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          phoneNumber: this.phoneNumber,
          bsn: this.bsn,
          role: 'CUSTOMER',
        });
        console.log('Registratie succesvol:', response.data);
        this.$router.push('/login'); // Redirect to login page after successful registration
      } catch (error) {
        console.error('Registratie mislukt:', error);
        this.errors.global = error.response?.data?.error || 'Registratie mislukt. Probeer het opnieuw.';
      }
    },
    validateEmail(email) {
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return re.test(email);
    },
    validatePhoneNumber(phoneNumber) {
      const re = /^\d{10}$/; // Assuming a 10-digit phone number
      return re.test(phoneNumber);
    }
  },
};
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.register-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 400px;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  font-weight: 600;
  font-size: 28px;
  letter-spacing: 0.5px;
  color: white;
}

.register-form {
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
  color: red;
  font-size: 14px;
  margin-top: 5px;
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
  .register-container {
    padding: 10px;
  }
  
  .register-title {
    font-size: 24px;
  }
  
  .register-form {
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
