<template>
  <div class="transaction-page">
    <div class="container-fluid py-4">
      <div class="page-header text-left text-white mb-4">
        <h1>Transactiegeschiedenis</h1>
        <p class="mb-0">
          {{ isEmployeeOrAdmin ? 'Bekijk alle transacties of filter op klant.' : 'Bekijk uw eigen transacties.' }}
        </p>
      </div>

      <div class="history-layout">
        <div class="card page-card mb-3">
          <div class="card-body">
            <div v-if="isEmployeeOrAdmin" class="form-row">
              <div class="form-group col-md-6 text-left">
                <label for="customer-scope" class="font-weight-bold">Klant</label>
                <select id="customer-scope" v-model="selectedCustomerId" class="form-control" :disabled="loading">
                  <option value="">Alle klanten / alle transacties</option>
                  <option v-for="customer in customers" :key="customer.id" :value="String(customer.id)">
                    {{ customer.firstName }} {{ customer.lastName }} - {{ customer.email }}
                  </option>
                </select>
              </div>
              <div class="form-group col-md-6 text-left">
                <label for="employee-account-scope" class="font-weight-bold">Rekening</label>
                <select id="employee-account-scope" v-model="selectedAccountIban" class="form-control" :disabled="loading || !scopeAccounts.length">
                  <option value="">Alle rekeningen binnen selectie</option>
                  <option v-for="account in scopeAccounts" :key="account.id || account.iban" :value="account.iban">
                    {{ accountLabel(account) }}
                  </option>
                </select>
              </div>
            </div>

            <div v-else>
              <AccountSelector
                v-model="selectedAccountIban"
                :accounts="scopeAccounts"
                label="Rekening"
                empty-label="Alle eigen rekeningen"
                :include-empty="true"
                :disabled="loading"
              />
            </div>

            <small class="text-muted d-block text-left">
              De IBAN-filter hieronder overschrijft de rekeningselectie, omdat de backend één IBAN-queryparameter ondersteunt.
            </small>
          </div>
        </div>

        <TransactionFilters :disabled="loading" class="mb-3" @apply="applyFilters" @clear="clearFilters" />

        <div class="card page-card">
          <div class="card-body">
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3">
              <h4 class="mb-2 mb-md-0">Resultaten</h4>
              <div class="form-inline justify-content-md-end">
                <label for="page-size" class="mr-2">Per pagina</label>
                <select id="page-size" v-model.number="pageSize" class="form-control form-control-sm" :disabled="loading" @change="changePageSize">
                  <option :value="5">5</option>
                  <option :value="10">10</option>
                  <option :value="25">25</option>
                  <option :value="50">50</option>
                </select>
              </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger text-left">
              {{ errorMessage }}
            </div>

            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="sr-only">Laden...</span>
              </div>
            </div>

            <div v-else-if="transactions.length === 0" class="alert alert-info text-left">
              Geen transacties gevonden.
            </div>

            <div v-else class="table-responsive">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>Datum/tijd</th>
                    <th>Type</th>
                    <th>Van IBAN</th>
                    <th>Naar IBAN</th>
                    <th class="text-right">Bedrag</th>
                    <th>Gestart door</th>
                    <th>Status</th>
                    <th class="text-right">Details</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="transaction in transactions" :key="transaction.id">
                    <td>{{ formatDate(transaction.timestamp) }}</td>
                    <td><span class="badge badge-secondary">{{ transaction.transactionType || '-' }}</span></td>
                    <td class="text-monospace">{{ transaction.senderIban || '-' }}</td>
                    <td class="text-monospace">{{ transaction.receiverIban || '-' }}</td>
                    <td class="text-right font-weight-bold">{{ formatMoney(transaction.amount) }}</td>
                    <td>{{ initiatedByLabel(transaction.initiatedByUserId) }}</td>
                    <td>{{ transaction.status || '-' }}</td>
                    <td class="text-right">
                      <button type="button" class="btn btn-sm btn-outline-primary" @click="openDetails(transaction.id)">
                        Bekijk
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3">
              <span class="text-muted mb-2 mb-md-0">
                Pagina {{ page }}{{ totalPages ? ` van ${totalPages}` : '' }} - {{ totalElements }} resultaten
              </span>
              <div>
                <button type="button" class="btn btn-outline-secondary mr-2" :disabled="loading || page === 1" @click="previousPage">
                  Vorige
                </button>
                <button type="button" class="btn btn-outline-secondary" :disabled="loading || !hasNextPage" @click="nextPage">
                  Volgende
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="selectedTransaction || detailLoading || detailError" class="card page-card mt-3">
          <div class="card-body text-left">
            <div class="d-flex justify-content-between align-items-start">
              <h4>Transactiedetails</h4>
              <button type="button" class="btn btn-sm btn-outline-secondary" @click="closeDetails">Sluiten</button>
            </div>

            <div v-if="detailLoading" class="py-3">Details laden...</div>
            <div v-else-if="detailError" class="alert alert-danger">{{ detailError }}</div>
            <dl v-else class="row mb-0">
              <dt class="col-sm-3">Transactie ID</dt>
              <dd class="col-sm-9">{{ selectedTransaction.id }}</dd>
              <dt class="col-sm-3">Datum/tijd</dt>
              <dd class="col-sm-9">{{ formatDate(selectedTransaction.timestamp) }}</dd>
              <dt class="col-sm-3">Type</dt>
              <dd class="col-sm-9">{{ selectedTransaction.transactionType || '-' }}</dd>
              <dt class="col-sm-3">Van</dt>
              <dd class="col-sm-9 text-monospace">{{ selectedTransaction.senderIban || '-' }}</dd>
              <dt class="col-sm-3">Naar</dt>
              <dd class="col-sm-9 text-monospace">{{ selectedTransaction.receiverIban || '-' }}</dd>
              <dt class="col-sm-3">Bedrag</dt>
              <dd class="col-sm-9">{{ formatMoney(selectedTransaction.amount) }}</dd>
              <dt class="col-sm-3">Valuta</dt>
              <dd class="col-sm-9">{{ selectedTransaction.currency || 'EUR' }}</dd>
              <dt class="col-sm-3">Gestart door</dt>
              <dd class="col-sm-9">{{ initiatedByLabel(selectedTransaction.initiatedByUserId) }}</dd>
              <dt class="col-sm-3">Status</dt>
              <dd class="col-sm-9">{{ selectedTransaction.status || '-' }}</dd>
              <dt class="col-sm-3">Omschrijving</dt>
              <dd class="col-sm-9">{{ selectedTransaction.description || '-' }}</dd>
            </dl>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../../store/auth';
import { getUsersPage } from '../../services/customerService';
import { getTransactionById, getTransactions } from '../../services/transactionService';
import { formatCurrency, formatDateTime, getErrorMessage } from '../../services/errorUtils';
import AccountSelector from './AccountSelector.vue';
import TransactionFilters from './TransactionFilters.vue';

export default {
  name: 'TransactionHistoryPage',
  components: {
    AccountSelector,
    TransactionFilters,
  },
  data() {
    return {
      users: [],
      selectedCustomerId: '',
      selectedAccountIban: '',
      filters: {},
      transactions: [],
      page: 1,
      pageSize: 10,
      totalPages: 0,
      totalElements: 0,
      loading: false,
      usersLoading: false,
      errorMessage: '',
      selectedTransaction: null,
      detailLoading: false,
      detailError: '',
    };
  },
  computed: {
    authStore() {
      return useAuthStore();
    },
    currentUser() {
      return this.authStore.currentUser || {};
    },
    isEmployeeOrAdmin() {
      return this.authStore.isEmployeeOrAdmin;
    },
    customers() {
      return this.users.filter((user) => user.role === 'CUSTOMER');
    },
    selectedCustomer() {
      return this.customers.find((customer) => String(customer.id) === String(this.selectedCustomerId)) || null;
    },
    scopeAccounts() {
      if (this.isEmployeeOrAdmin) {
        return this.selectedCustomer?.accounts || [];
      }

      return this.currentUser.accounts || [];
    },
    hasNextPage() {
      return this.page < this.totalPages;
    },
  },
  watch: {
    selectedCustomerId() {
      this.selectedAccountIban = '';
      this.page = 1;
      this.fetchTransactions();
    },
    selectedAccountIban() {
      this.page = 1;
      this.fetchTransactions();
    },
  },
  async created() {
    if (this.isEmployeeOrAdmin) {
      await this.fetchUsers();
    }

    await this.fetchTransactions();
  },
  methods: {
    accountLabel(account) {
      const type = account.accountType === 'CHECKING' ? 'Betaalrekening' : 'Spaarrekening';
      return `${account.iban} - ${type}`;
    },
    buildParams() {
      const params = {
        page: Math.max(this.page - 1, 0),
        size: this.pageSize,
        ...this.filters,
      };

      if (this.isEmployeeOrAdmin && this.selectedCustomerId) {
        params.customer_id = this.selectedCustomerId;
      }

      if (!params.iban && this.selectedAccountIban) {
        params.iban = this.selectedAccountIban;
      }

      return params;
    },
    async fetchUsers() {
      this.usersLoading = true;

      try {
        const response = await getUsersPage({
          approved: true,
          page: 0,
          size: 1000,
        });
        this.users = response.data?.content || [];
      } catch (error) {
        this.errorMessage = getErrorMessage(error, 'Gebruikers ophalen is mislukt.');
      } finally {
        this.usersLoading = false;
      }
    },
    async fetchTransactions() {
      this.loading = true;
      this.errorMessage = '';

      try {
        const response = await getTransactions(this.buildParams());
        const pageData = response.data || {};

        this.transactions = pageData.content || [];
        this.totalPages = pageData.totalPages || 0;
        this.totalElements = pageData.totalElements || 0;
        this.page = (pageData.number ?? Math.max(this.page - 1, 0)) + 1;
      } catch (error) {
        this.transactions = [];
        this.totalPages = 0;
        this.totalElements = 0;
        this.errorMessage = getErrorMessage(error, 'Transacties ophalen is mislukt.');
      } finally {
        this.loading = false;
      }
    },
    applyFilters(filters) {
      this.filters = filters;
      this.page = 1;
      this.fetchTransactions();
    },
    clearFilters() {
      this.filters = {};
      this.page = 1;
      this.fetchTransactions();
    },
    changePageSize() {
      this.page = 1;
      this.fetchTransactions();
    },
    previousPage() {
      if (this.page <= 1) {
        return;
      }

      this.page -= 1;
      this.fetchTransactions();
    },
    nextPage() {
      if (!this.hasNextPage) {
        return;
      }

      this.page += 1;
      this.fetchTransactions();
    },
    async openDetails(transactionId) {
      this.detailLoading = true;
      this.detailError = '';
      this.selectedTransaction = null;

      try {
        const response = await getTransactionById(transactionId);
        this.selectedTransaction = response.data;
      } catch (error) {
        this.detailError = getErrorMessage(error, 'Transactiedetails ophalen is mislukt.');
      } finally {
        this.detailLoading = false;
      }
    },
    closeDetails() {
      this.selectedTransaction = null;
      this.detailError = '';
      this.detailLoading = false;
    },
    initiatedByLabel(userId) {
      if (!userId) {
        return '-';
      }

      if (String(userId) === String(this.currentUser.id)) {
        return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
      }

      const user = this.users.find((item) => String(item.id) === String(userId));
      return user ? `${user.firstName} ${user.lastName}` : `Gebruiker #${userId}`;
    },
    formatMoney(value) {
      return formatCurrency(value);
    },
    formatDate(value) {
      return formatDateTime(value);
    },
  },
};
</script>

<style scoped>
.transaction-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.page-header h1 {
  font-weight: 700;
}

.history-layout {
  max-width: 1280px;
  margin: 0 auto;
}

.page-card {
  border: 0;
  border-radius: 14px;
  box-shadow: 0 16px 40px rgba(31, 45, 61, 0.2);
}

.table {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .container-fluid {
    padding-left: 12px;
    padding-right: 12px;
  }
}
</style>
