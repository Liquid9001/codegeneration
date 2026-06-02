<template>
  <div class="accountdetails-container">
    <h2>Bankrekening Details</h2>
    <button @click="goToBack" class="btn btn-primary">Terug naar Bankrekeningen</button>
    <div v-if="loading" class="spinner-border text-primary" role="status">
    </div>
    <div v-else-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    <div v-else>
      <table class="table table-striped">
        <tbody>
          <tr>
            <th>Gebruiker</th>
            <td>{{ account.firstName }} {{ account.lastName }}</td>
          </tr>
          <tr>
            <th>IBAN</th>
            <td>{{ account.iban }}</td>
          </tr>
          <tr>
            <th>Type Rekening</th>
            <td>{{ account.accountType == "CHECKING" ? "Betaalrekening" : "Spaarrekening" }}</td>
          </tr>
          <tr>
            <th>Balans</th>
            <td>€{{ account.balance.toFixed(2) }}</td>
          </tr>
          <tr>
            <th>Dagelijkse Overboekingslimiet</th>
            <td>€{{ account.dailyTransferLimit.toFixed(2) }}</td>
          </tr>
          <tr>
            <th>Absolute Overboekingslimiet</th>
            <td>€{{ account.absoluteTransferLimit.toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../../store/auth';

export default {
  name: 'AccountDetails',
  data() {
    return {
      account: null,
      loading: true,
      error: null
    };
  },
  async created() {
    await this.fetchAccountDetails();
  },
  methods: {
    async fetchAccountDetails() {
      try {
        const apiUrl = import.meta.env.VITE_API_URL;
        const token = useAuthStore().token;
        const accountId = this.$route.params.accountId;
        const response = await axios.get(`${apiUrl}/accounts/${accountId}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        this.account = response.data;
      } catch (error) {
        console.error('Error fetching account details:', error);
        this.error = 'Er is een fout opgetreden bij het ophalen van de bankrekeningdetails.';
      } finally {
        this.loading = false;
      }
    },
    goToBack() {
      this.$router.push('/admin/bankaccounts');
    }
  }
};
</script>

<style scoped>
.accountdetails-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.accountdetails-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  letter-spacing: 0.5px;
}

.btn-primary {
  position: absolute;
  top: 100px; /* Aangepast om niet te overlappen met de navbar */
  right: 10px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.btn-primary:hover {
  background: linear-gradient(to right, #00f2fe 0%, #4facfe 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.spinner-border {
  margin-top: 20px;
}

.alert {
  margin-top: 20px;
}

.table {
  margin-top: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5px);
  overflow: hidden;
}

.table tbody {
  background: rgba(255, 255, 255, 0.05);
}

.table th,
.table td {
  padding: 12px 15px;
  color: white;
  font-size: 14px;
}

@media (max-width: 768px) {
  .accountdetails-container {
    padding: 15px;
  }

  .accountdetails-container h2 {
    font-size: 20px;
  }

  .btn-primary {
    padding: 6px 12px;
    font-size: 12px;
  }

  .table th,
  .table td {
    padding: 10px 10px;
    font-size: 12px;
  }
}
</style>
