<template>
  <div class="transaction-page">
    <div class="container py-4">
      <div class="page-header text-left text-white mb-4">
        <h1>Overboeking</h1>
        <p class="mb-0">Maak een EUR-overboeking vanaf een van uw eigen rekeningen.</p>
      </div>

      <div v-if="!accounts.length" class="alert alert-warning text-left">
        U heeft nog geen actieve rekeningen. Een medewerker moet uw account eerst goedkeuren.
      </div>

      <div v-else class="card page-card">
        <div class="card-body">
          <div v-if="successMessage" class="alert alert-success text-left">
            {{ successMessage }}
          </div>
          <div v-if="errorMessage" class="alert alert-danger text-left">
            {{ errorMessage }}
          </div>

          <form @submit.prevent="submitTransfer">
            <AccountSelector
              v-model="senderIban"
              :accounts="senderAccounts"
              label="Van rekening"
              empty-label="Selecteer de verzendrekening"
              required
              :disabled="loading"
              @account-selected="senderAccount = $event"
            />

            <div class="form-group text-left">
              <label class="font-weight-bold d-block">Type overboeking</label>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="transfer-type-own"
                  v-model="transferType"
                  type="radio"
                  class="custom-control-input"
                  value="own"
                  :disabled="loading"
                />
                <label class="custom-control-label" for="transfer-type-own">Naar eigen rekening</label>
              </div>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="transfer-type-external"
                  v-model="transferType"
                  type="radio"
                  class="custom-control-input"
                  value="external"
                  :disabled="loading"
                />
                <label class="custom-control-label" for="transfer-type-external">Naar andere klant</label>
              </div>
            </div>

            <div v-if="transferType === 'own'">
              <AccountSelector
                v-model="receiverIban"
                :accounts="ownReceiverAccounts"
                label="Naar eigen rekening"
                empty-label="Selecteer de ontvangende rekening"
                required
                :disabled="loading"
              />
            </div>

            <div v-else class="external-transfer-block">
              <div class="alert alert-info text-left">
                Overboeken naar een andere klant kan alleen vanaf uw betaalrekening. Zoek op naam of vul de IBAN handmatig in.
              </div>

              <IbanSearch @selected="selectExternalIban" />

              <div class="form-group text-left mt-3">
                <label for="receiver-iban" class="font-weight-bold">Ontvangende IBAN</label>
                <input
                  id="receiver-iban"
                  v-model.trim="receiverIban"
                  type="text"
                  class="form-control text-monospace"
                  placeholder="NL61ABNA0123456790"
                  required
                  :disabled="loading"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group col-md-6 text-left">
                <label for="transfer-amount" class="font-weight-bold">Bedrag</label>
                <div class="input-group">
                  <div class="input-group-prepend">
                    <span class="input-group-text">EUR</span>
                  </div>
                  <input
                    id="transfer-amount"
                    v-model="amount"
                    type="number"
                    min="0.01"
                    step="0.01"
                    class="form-control"
                    required
                    :disabled="loading"
                  />
                </div>
              </div>
              <div class="form-group col-md-6 text-left">
                <label for="transfer-description" class="font-weight-bold">Omschrijving</label>
                <input
                  id="transfer-description"
                  v-model.trim="description"
                  type="text"
                  maxlength="255"
                  class="form-control"
                  placeholder="Optioneel"
                  :disabled="loading"
                />
              </div>
            </div>

            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3">
              <router-link to="/" class="btn btn-outline-secondary mb-2 mb-md-0">Terug naar dashboard</router-link>
              <button type="submit" class="btn btn-primary" :disabled="loading">
                {{ loading ? 'Overboeking verwerken...' : 'Overboeking uitvoeren' }}
              </button>
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
import { getErrorMessage } from '../../services/errorUtils';
import { transfer } from '../../services/transactionService';
import AccountSelector from './AccountSelector.vue';
import IbanSearch from './IbanSearch.vue';

export default {
  name: 'TransferPage',
  components: {
    AccountSelector,
    IbanSearch,
  },
  data() {
    return {
      transferType: 'own',
      senderIban: '',
      receiverIban: '',
      senderAccount: null,
      amount: '',
      description: '',
      loading: false,
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
    accounts() {
      return this.currentUser.accounts || [];
    },
    senderAccounts() {
      if (this.transferType === 'external') {
        return this.accounts.filter((account) => account.accountType === 'CHECKING');
      }

      return this.accounts;
    },
    ownReceiverAccounts() {
      return this.accounts.filter((account) => account.iban !== this.senderIban);
    },
  },
  watch: {
    transferType() {
      this.receiverIban = '';
      this.ensureValidSender();
    },
    senderIban() {
      this.senderAccount = this.accounts.find((account) => account.iban === this.senderIban) || null;

      if (this.receiverIban === this.senderIban) {
        this.receiverIban = '';
      }
    },
    accounts() {
      this.ensureValidSender();
    },
  },
  created() {
    this.ensureValidSender();
  },
  methods: {
    ensureValidSender() {
      if (this.senderAccounts.some((account) => account.iban === this.senderIban)) {
        return;
      }

      this.senderIban = this.senderAccounts[0]?.iban || '';
      this.senderAccount = this.senderAccounts[0] || null;
    },
    selectExternalIban(result) {
      this.receiverIban = result.iban;
    },
    validateTransfer() {
      if (!this.senderIban) {
        return 'Selecteer een verzendrekening.';
      }

      if (!this.receiverIban) {
        return 'Selecteer of vul een ontvangende IBAN in.';
      }

      if (this.senderIban === this.receiverIban) {
        return 'De verzendende en ontvangende rekening mogen niet hetzelfde zijn.';
      }

      if (!this.amount || Number(this.amount) <= 0) {
        return 'Vul een geldig bedrag groter dan 0 in.';
      }

      if (this.transferType === 'external' && this.senderAccount?.accountType !== 'CHECKING') {
        return 'Overboeken naar een andere klant kan alleen vanaf een betaalrekening.';
      }

      return '';
    },
    async refreshCurrentUser() {
      const userId = this.currentUser.id;

      if (!userId) {
        return;
      }

      const response = await getUserWithAccounts(userId);
      this.authStore.login(this.authStore.token, response.data);
    },
    async submitTransfer() {
      this.errorMessage = '';
      this.successMessage = '';

      const validationError = this.validateTransfer();
      if (validationError) {
        this.errorMessage = validationError;
        return;
      }

      this.loading = true;

      try {
        const response = await transfer({
          senderIban: this.senderIban,
          receiverIban: this.receiverIban,
          amount: this.amount,
          description: this.description,
          initiatedByUserId: this.currentUser.id,
        });

        await this.refreshCurrentUser();
        this.amount = '';
        this.description = '';
        this.successMessage = `Overboeking verwerkt. Transactie #${response.data.id}.`;
      } catch (error) {
        this.errorMessage = getErrorMessage(error, 'Overboeking is mislukt.');
      } finally {
        this.loading = false;
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

.external-transfer-block {
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  background: #f8f9fa;
}
</style>
