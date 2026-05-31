<template>
  <div class="approve-user-container">
    <h2>Gebruiker goedkeuren</h2>
    <form @submit.prevent="approveUser" class="form-inline">
      <div class="form-group">
        <label for="checkingAccountAbsoluteLimit">Absoluut betaallimiet Betaalrekening:</label>
        <input type="text" id="checkingAccountAbsoluteLimit" v-model="checkingAccount.absoluteTransferLimit" required />
      </div>
      <div class="form-group">
        <label for="checkingAccountDailyLimit">Dagelijkse betaallimiet Betaalrekening:</label>
        <input type="text" id="checkingAccountDailyLimit" v-model="checkingAccount.dailyTransferLimit" required />
      </div>
      <div class="form-group">
        <label for="savingsAccountAbsoluteLimit">Absoluut betaallimiet Spaarrekening:</label>
        <input type="text" id="savingsAccountAbsoluteLimit" v-model="savingsAccount.absoluteTransferLimit" required />
      </div>
      <div class="form-group">
        <label for="savingsAccountDailyLimit">Dagelijkse betaallimiet Spaarrekening:</label>
        <input type="text" id="savingsAccountDailyLimit" v-model="savingsAccount.dailyTransferLimit" required />
      </div>
      <button type="submit" class="btn btn-success">Goedkeuren</button>
      <button @click="goBack" class="btn btn-secondary">Terug</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../../store/auth';

export default {
  name: 'ApproveUser',
  data() {
    return {
      checkingAccount: {
        absoluteTransferLimit: '',
        dailyTransferLimit: ''
      },
      savingsAccount: {
        absoluteTransferLimit: '',
        dailyTransferLimit: ''
      }
    };
  },
  methods: {
    async approveUser() {
      try {
        const token = useAuthStore().token;
        const userId = this.$route.params.id;
        const response = await axios.post(`http://localhost:8080/users/${userId}`, {
          checkingAccount: this.checkingAccount,
          savingsAccount: this.savingsAccount
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        console.log('User approved:', response.data);
        this.$router.push({ name: 'AdminUsers' });
      } catch (error) {
        console.error('Error approving user:', error);
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.approve-user-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.approve-user-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  letter-spacing: 0.5px;
}

.form-inline {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-group {
  margin-bottom: 15px;
  width: 100%;
}

label {
  display: block;
  margin-bottom: 5px;
}

input[type="text"] {
  width: 100%;
  padding: 8px;
  border: none;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

input[type="text"]:focus {
  outline: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.btn-success,
.btn-secondary {
  margin-top: 15px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.btn-success {
  background: linear-gradient(to right, #28a745 0%, #218838 100%);
  color: white;
}

.btn-success:hover {
  background: linear-gradient(to right, #218838 0%, #28a745 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  background: linear-gradient(to right, #adb5bd 0%, #6c757d 100%);
  color: white;
}

.btn-secondary:hover {
  background: linear-gradient(to right, #6c757d 0%, #adb5bd 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .approve-user-container {
    padding: 15px;
  }

  .approve-user-container h2 {
    font-size: 20px;
  }

  input[type="text"] {
    width: 100%;
  }
}
</style>
