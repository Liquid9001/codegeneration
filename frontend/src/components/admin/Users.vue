<template>
  <div class="users-container">
    <h2>Niet-goedgekeurde Gebruikers</h2>
    <div class="pagination-controls">
      <button @click="previousPage" :disabled="offset === 0" class="btn btn-secondary">Vorige</button>
      <span>Pagina {{ currentPage }} van {{ totalPages }}</span>
      <button @click="nextPage" :disabled="offset + limit >= totalUsers" class="btn btn-secondary">Volgende</button>
    </div>
    <table class="table table-striped">
      <thead>
        <tr>
          <th>Voornaam</th>
          <th>Achternaam</th>
          <th>Email</th>
          <th>BSN</th>
          <th>Telefoonnummer</th>
          <th>Aangemaakt op</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.firstName }}</td>
          <td>{{ user.lastName }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.bsn }}</td>
          <td>{{ user.phoneNumber }}</td>
          <td>{{ formatDate(user.createdAt) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../../store/auth';

export default {
  name: 'Users',
  data() {
    return {
      users: [],
      totalUsers: 0,
      offset: 0,
      limit: 10,
      currentPage: 1
    };
  },
  async mounted() {
    await this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      try {
        const token = useAuthStore().token;
        const response = await axios.get(`http://localhost:8080/users?offset=${this.offset}&limit=${this.limit}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        
        // Filter users that are not approved
        this.users = response.data.filter(user => !user.approved);
        this.totalUsers = this.users.length;
        this.totalPages = Math.ceil(this.totalUsers / this.limit);
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    },
    previousPage() {
      if (this.offset > 0) {
        this.offset -= this.limit;
        this.currentPage--;
        this.fetchUsers();
      }
    },
    nextPage() {
      if (this.offset + this.limit < this.totalUsers) {
        this.offset += this.limit;
        this.currentPage++;
        this.fetchUsers();
      }
    },
    formatDate(dateString) {
      const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
      return new Date(dateString).toLocaleDateString('nl-NL', options);
    }
  }
};
</script>

<style scoped>
.users-container {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  margin: 20px auto;
  max-width: 1000px;
}

.users-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  letter-spacing: 0.5px;
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
  .users-container {
    padding: 15px;
  }

  .users-container h2 {
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
