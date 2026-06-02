<template>
  <div class="bank-account">
    <div class="account-info">
      <h4 class="account-iban">{{ account.iban }}</h4>
      <p class="account-balance">€ {{ account.balance | currencyFormat }}</p>
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
  justify-content: space-between;
  margin-top: 15px;
  padding: 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  width: 100%;
}

.bank-account:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.15);
}

.account-info {
  display: flex;
  flex-direction: column;
}

.account-iban {
  font-size: 1.2em;
  color: white;
  margin-bottom: 5px;
  font-weight: 600;
}

.account-balance {
  font-size: 1.1em;
  color: #e0e0e0;
  margin-bottom: 5px;
  font-weight: 500;
}

.checking-account,
.savings-account {
  display: block;
  font-size: 0.9em;
  margin-top: 5px;
  padding: 5px 10px;
  border-radius: 20px;
  font-weight: 600;
  text-align: center;
  width: fit-content;
}

.checking-account {
  color: #4facfe;
  background: rgba(79, 172, 254, 0.2);
}

.savings-account {
  color: #00f2fe;
  background: rgba(0, 242, 254, 0.2);
}

@media (max-width: 768px) {
  .bank-account {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .account-iban {
    font-size: 1.1em;
  }
  
  .account-balance {
    font-size: 1em;
  }
}
</style>
