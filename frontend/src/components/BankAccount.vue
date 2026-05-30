<template>
  <div class="bank-account">
    <div class="account-info">
      <h4>{{ account.iban }}</h4>
      <p>€ {{ account.balance | currencyFormat }}</p>
    </div>
    <span :class="accountTypeClass">{{ accountTypeLabel }}</span>
  </div>
</template>

<script>
export default {
  name: 'BankAccount',
  props: {
    account: {
      type: Object,
      required: true
    }
  },
  computed: {
    accountTypeLabel() {
      if (this.account.accountType === 'CHECKING') {
        return 'Overboekingsrekening';
      } else if (this.account.accountType === 'SAVINGS') {
        return 'Spaarrekening';
      }
      return '';
    },
    accountTypeClass() {
      if (this.account.accountType === 'CHECKING') {
        return 'checking-account';
      } else if (this.account.accountType === 'SAVINGS') {
        return 'savings-account';
      }
      return '';
    }
  },
  filters: {
    currencyFormat(value) {
      if (!value) return '0,00';
      const parts = value.toString().split('.');
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, '.');
      return parts.join(',');
    }
  }
};
</script>

<style scoped>
.bank-account {
  display: flex;
  align-items: center;
  margin-top: 15px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.account-info {
  display: flex;
  flex-direction: column;
  margin-right: 15px;
}

h4 {
  font-size: 1.2em;
  color: #333;
  margin-bottom: 5px;
}

p {
  font-size: 1em;
  color: #666;
  margin-bottom: 5px;
}

.checking-account,
.savings-account {
  display: block;
  font-size: 0.9em;
  margin-top: 5px;
}

.checking-account {
  color: #007bff;
}

.savings-account {
  color: #28a745;
}
</style>
