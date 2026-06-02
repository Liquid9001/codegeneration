<template>
  <div class="bankaccounts-container">
    <h2>Bankrekeningen</h2>
    <button @click="goToDashboard" class="btn btn-primary">Terug naar Dashboard</button>
    <div class="pagination-controls">
      <button @click="previousPage" :disabled="offset === 0" class="btn btn-secondary">Vorige</button>
      <span>Pagina {{ currentPage }} van {{ totalPages }}</span>
      <button @click="nextPage" :disabled="offset + limit >= totalAccounts" class="btn btn-secondary">Volgende</button>
    </div>
    <table class="table table-striped">
      <thead>
        <tr>
          <th>Gebruiker</th>
          <th>IBAN</th>
          <th>Type Rekening</th>
          <th>Balans</th>
          <th>Dagelijkse Overboekingslimiet</th>
          <th>Absolute Overboekingslimiet</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="account in displayedAccounts" :key="account.id">
          <td>{{ account.user.firstName }} {{ account.user.lastName }}</td>
          <td>{{ account.iban }}</td>
          <td>{{ account.accountType == "CHECKING" ? "Betaalrekening" : "Spaarrekening" }}</td>
          <td>€{{ account.balance.toFixed(2) }}</td>
          <td>€{{ account.dailyTransferLimit.toFixed(2) }}</td>
          <td>€{{ account.absoluteTransferLimit.toFixed(2) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../../store/auth';

export default {
  name: 'BankAccounts',
  data() {
    return {
      accounts: [],
      totalAccounts: 0,
      offset: 0,
      limit: 10,
      currentPage: 1
    };
  },
  async mounted() {
    await this.fetchAccounts();
  },
  methods: {
    async fetchAccounts() {
      try {
        const apiUrl = import.meta.env.VITE_API_URL
        const token = useAuthStore().token;
        const response = await axios.get(`${apiUrl}/users?offset=${this.offset}&limit=${this.limit}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        // Combine all accounts from users
        this.accounts = response.data.reduce((acc, user) => {
          return acc.concat(user.accounts.map(account => ({ ...account, user })));
        }, []);
        this.totalAccounts = this.accounts.length;
        this.totalPages = Math.ceil(this.totalAccounts / this.limit);
      } catch (error) {
        console.error('Error fetching accounts:', error);
      }
    },
    previousPage() {
      if (this.offset > 0) {
        this.offset -= this.limit;
        this.currentPage--;
        this.fetchAccounts();
      }
    },
    nextPage() {
      if (this.offset + this.limit < this.totalAccounts) {
        this.offset += this.limit;
        this.currentPage++;
        this.fetchAccounts();
      }
    },
    goToDashboard() {
      this.$router.push('/');
    }
  },
  computed: {
    displayedAccounts() {
      return this.accounts.slice(this.offset, this.offset + this.limit);
    }
  }
};
</script>

<style scoped>
.bankaccounts-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.bankaccounts-container h2 {
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

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin: 20px 0;
}

.pagination-controls button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.pagination-controls button:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}

.pagination-controls button:hover:not(:disabled) {
  background: linear-gradient(to right, #00f2fe 0%, #4facfe 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.pagination-controls span {
  font-weight: 500;
  color: #f8f9fa;
}

.table {
  margin-top: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5px);
  overflow: hidden;
}

.table thead {
  background: rgba(255, 255, 255, 0.05);
}

.table th,
.table td {
  padding: 12px 15px;
  color: white;
  font-size: 14px;
}

.table tr:hover {
  background: rgba(255, 255, 255, 0.08);
}

@media (max-width: 768px) {
  .bankaccounts-container {
    padding: 15px;
  }

  .bankaccounts-container h2 {
    font-size: 20px;
  }

  .pagination-controls button {
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
