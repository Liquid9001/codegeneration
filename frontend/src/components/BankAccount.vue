<template>
  <div class="bank-account">
    <h4>{{ account.iban }}</h4>
    <p>Saldo: {{ account.balance }}</p>
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
  }
};
</script>

<style scoped>
.bank-account {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
