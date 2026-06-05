<template>
  <div class="card filter-card">
    <div class="card-body">
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3">
        <h5 class="card-title mb-2 mb-md-0">Transacties filteren</h5>
        <button type="button" class="btn btn-link p-0" :disabled="disabled" @click="clearFilters">
          Filters wissen
        </button>
      </div>

      <div class="form-row">
        <div class="form-group col-md-3 text-left">
          <label for="filter-start-date">Startdatum</label>
          <input id="filter-start-date" v-model="startDate" type="date" class="form-control" :disabled="disabled" />
        </div>
        <div class="form-group col-md-3 text-left">
          <label for="filter-end-date">Einddatum</label>
          <input id="filter-end-date" v-model="endDate" type="date" class="form-control" :disabled="disabled" />
        </div>
        <div class="form-group col-md-3 text-left">
          <label for="filter-amount-comparison">Bedrag</label>
          <select id="filter-amount-comparison" v-model="amountComparison" class="form-control" :disabled="disabled">
            <option value="">Geen bedragfilter</option>
            <option value="less">Minder dan</option>
            <option value="greater">Meer dan</option>
            <option value="equal">Gelijk aan</option>
          </select>
        </div>
        <div class="form-group col-md-3 text-left">
          <label for="filter-amount-value">Bedrag in EUR</label>
          <input
            id="filter-amount-value"
            v-model="amountValue"
            type="number"
            min="0.01"
            step="0.01"
            class="form-control"
            :disabled="disabled || !amountComparison"
            placeholder="100.00"
          />
        </div>
      </div>

      <div class="form-row align-items-end">
        <div class="form-group col-md-5 text-left">
          <label for="filter-iban">IBAN to/from</label>
          <input
            id="filter-iban"
            v-model.trim="iban"
            type="text"
            class="form-control text-monospace"
            :disabled="disabled"
            placeholder="NL91ABNA0417164300"
          />
        </div>
        <div class="form-group col-md-4 text-left">
          <label for="filter-transaction-type">Transactietype</label>
          <select id="filter-transaction-type" v-model="transactionType" class="form-control" :disabled="disabled">
            <option value="">Alle types</option>
            <option value="TRANSFER">Transfer</option>
            <option value="DEPOSIT">Deposit</option>
            <option value="WITHDRAWAL">Withdrawal</option>
          </select>
        </div>
        <div class="form-group col-md-3">
          <button type="button" class="btn btn-primary btn-block" :disabled="disabled" @click="applyFilters">
            Filters toepassen
          </button>
        </div>
      </div>

      <div v-if="error" class="alert alert-danger py-2 mb-0 text-left">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script>
const toStartDateTime = (date) => (date ? `${date}T00:00:00` : '');
const toEndDateTime = (date) => (date ? `${date}T23:59:59` : '');
const toAmount = (value) => Number(value || 0);
const toMoney = (value) => value.toFixed(2);

export default {
  name: 'TransactionFilters',
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['apply', 'clear'],
  data() {
    return {
      startDate: '',
      endDate: '',
      amountComparison: '',
      amountValue: '',
      iban: '',
      transactionType: '',
      error: '',
    };
  },
  methods: {
    applyFilters() {
      this.error = '';

      if (this.startDate && this.endDate && this.startDate > this.endDate) {
        this.error = 'De startdatum mag niet na de einddatum liggen.';
        return;
      }

      if (this.amountComparison && (!this.amountValue || toAmount(this.amountValue) <= 0)) {
        this.error = 'Vul een geldig bedrag groter dan 0 in.';
        return;
      }

      const filters = {
        start_date: toStartDateTime(this.startDate),
        end_date: toEndDateTime(this.endDate),
        iban: this.iban,
        transaction_type: this.transactionType,
      };

      if (this.amountComparison) {
        const amount = toAmount(this.amountValue);

        if (this.amountComparison === 'less') {
          filters.max_amount = toMoney(Math.max(amount - 0.01, 0));
        }

        if (this.amountComparison === 'greater') {
          filters.min_amount = toMoney(amount + 0.01);
        }

        if (this.amountComparison === 'equal') {
          filters.min_amount = toMoney(amount);
          filters.max_amount = toMoney(amount);
        }
      }

      this.$emit('apply', filters);
    },
    clearFilters() {
      this.startDate = '';
      this.endDate = '';
      this.amountComparison = '';
      this.amountValue = '';
      this.iban = '';
      this.transactionType = '';
      this.error = '';
      this.$emit('clear');
    },
  },
};
</script>

<style scoped>
.filter-card {
  border: 0;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(31, 45, 61, 0.12);
}
</style>
