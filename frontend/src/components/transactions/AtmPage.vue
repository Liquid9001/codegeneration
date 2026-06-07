<template>
  <div class="transaction-page">
    <div class="container py-4">
      <div class="page-header text-left text-white mb-4">
        <h1>Mock ATM</h1>
        <p class="mb-0">Stort of neem EUR op van uw geselecteerde rekening.</p>
      </div>

      <div v-if="!isCustomer" class="alert alert-warning text-left">
        De ATM is alleen beschikbaar voor klanten. Log uit en meld u aan met een klantaccount.
      </div>

      <div v-else-if="!accounts.length" class="alert alert-warning text-left">
        Er zijn geen actieve rekeningen beschikbaar voor dit klantaccount.
      </div>

      <div v-else class="card page-card">
        <div class="card-body">
          <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-start mb-3">
            <div class="text-left">
              <h4 class="mb-1">ATM transacties</h4>
              <p class="text-muted mb-0">{{ currentUser.firstName }} {{ currentUser.lastName }}</p>
            </div>
            <button type="button" class="btn btn-outline-secondary mt-2 mt-md-0" @click="logoutAtm">ATM logout</button>
          </div>

          <div v-if="successMessage" class="alert alert-success text-left">
            {{ successMessage }}
          </div>
          <div v-if="errorMessage" class="alert alert-danger text-left">
            {{ errorMessage }}
          </div>

          <AccountSelector
            v-model="selectedIban"
            :accounts="accounts"
            label="Rekening"
            empty-label="Selecteer een rekening"
            required
            :disabled="isActionLoading"
          />

          <div v-if="selectedAccount" class="account-summary text-left mb-4">
            <span class="badge badge-light mr-2">{{ accountTypeLabel(selectedAccount) }}</span>
            <span class="font-weight-bold">{{ selectedAccount.iban }}</span>
            <span class="d-block mt-2">Huidig saldo: {{ formatMoney(selectedAccount.balance) }}</span>
          </div>

          <form class="atm-action-card" @submit.prevent>
            <h5>Contant geld</h5>
            <p class="text-muted">Vul een bedrag in en kies of u wilt storten of opnemen.</p>
            <div class="form-group text-left">
              <label for="atm-amount" class="font-weight-bold">Bedrag</label>
              <div class="input-group">
                <div class="input-group-prepend">
                  <span class="input-group-text">EUR</span>
                </div>
                <input
                  id="atm-amount"
                  v-model="amount"
                  type="number"
                  min="0.01"
                  step="0.01"
                  class="form-control"
                  required
                  :disabled="isActionLoading"
                />
              </div>
            </div>

            <div class="row atm-action-buttons">
              <div class="col-md-6 mb-2 mb-md-0">
                <button type="button" class="btn btn-success btn-block" :disabled="isActionLoading" @click="submitDeposit">
                  {{ actionLoading === 'deposit' ? 'Storting verwerken...' : 'Storten' }}
                </button>
              </div>
              <div class="col-md-6">
                <button type="button" class="btn btn-danger btn-block" :disabled="isActionLoading" @click="submitWithdrawal">
                  {{ actionLoading === 'withdrawal' ? 'Opname verwerken...' : 'Opnemen' }}
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../../store/auth';
import { getUserWithAccounts } from '../../services/authService';
import { deposit, withdrawal } from '../../services/transactionService';
import { formatCurrency, getErrorMessage } from '../../services/errorUtils';
import AccountSelector from './AccountSelector.vue';

export default {
  name: 'AtmPage',
  components: {
    AccountSelector,
  },
  data() {
    return {
      selectedIban: '',
      amount: '',
      actionLoading: '',
      successMessage: '',
      errorMessage: '',
    };
  },
  computed: {
    authStore() {
      return useAuthStore();
    },
    currentUser() {
      return this.authStore.currentUser || {};
    },
    isCustomer() {
      return this.currentUser.role === 'CUSTOMER';
    },
    accounts() {
      return this.currentUser.accounts || [];
    },
    selectedAccount() {
      return this.accounts.find((account) => account.iban === this.selectedIban) || null;
    },
    isActionLoading() {
      return Boolean(this.actionLoading);
    },
  },
  watch: {
    accounts() {
      this.ensureSelectedAccount();
    },
  },
  created() {
    this.ensureSelectedAccount();
  },
  methods: {
    accountTypeLabel(account) {
      return account.accountType === 'CHECKING' ? 'Betaalrekening' : 'Spaarrekening';
    },
    formatMoney(value) {
      return formatCurrency(value);
    },
    ensureSelectedAccount() {
      if (this.accounts.some((account) => account.iban === this.selectedIban)) {
        return;
      }

      this.selectedIban = this.accounts[0]?.iban || '';
    },
    logoutAtm() {
      this.authStore.logout();
      this.selectedIban = '';
      this.amount = '';
      this.successMessage = '';
      this.errorMessage = '';
      this.$router.push({ name: 'Login', query: { scope: 'atm' } });
    },
    validateAction(amount) {
      if (!this.selectedIban) {
        return 'Selecteer eerst een rekening.';
      }

      if (!amount || Number(amount) <= 0) {
        return 'Vul een geldig bedrag groter dan 0 in.';
      }

      return '';
    },
    async refreshCurrentUser() {
      const response = await getUserWithAccounts(this.currentUser.id);
      this.authStore.login(this.authStore.token, response.data);
      this.ensureSelectedAccount();
    },
    async submitDeposit() {
      this.errorMessage = '';
      this.successMessage = '';

      const validationError = this.validateAction(this.amount);
      if (validationError) {
        this.errorMessage = validationError;
        return;
      }

      this.actionLoading = 'deposit';

      try {
        const response = await deposit({
          receiverIban: this.selectedIban,
          amount: this.amount,
          description: 'ATM deposit',
          initiatedByUserId: this.currentUser.id,
        });

        await this.refreshCurrentUser();
        this.amount = '';
        this.successMessage = `Storting verwerkt. Transactie #${response.data.id}.`;
      } catch (error) {
        this.errorMessage = getErrorMessage(error, 'Storting is mislukt.');
      } finally {
        this.actionLoading = '';
      }
    },
    async submitWithdrawal() {
      this.errorMessage = '';
      this.successMessage = '';

      const validationError = this.validateAction(this.amount);
      if (validationError) {
        this.errorMessage = validationError;
        return;
      }

      this.actionLoading = 'withdrawal';

      try {
        const response = await withdrawal({
          senderIban: this.selectedIban,
          amount: this.amount,
          description: 'ATM withdrawal',
          initiatedByUserId: this.currentUser.id,
        });

        await this.refreshCurrentUser();
        this.amount = '';
        this.successMessage = `Opname verwerkt. Transactie #${response.data.id}.`;
      } catch (error) {
        this.errorMessage = getErrorMessage(error, 'Opname is mislukt.');
      } finally {
        this.actionLoading = '';
      }
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

.page-card {
  border: 0;
  border-radius: 14px;
  box-shadow: 0 16px 40px rgba(31, 45, 61, 0.25);
}

.account-summary,
.atm-action-card {
  border: 1px solid #e9ecef;
  border-radius: 12px;
  background: #f8f9fa;
  padding: 16px;
}
</style>
