<template>
  <div class="form-group text-left">
    <label :for="inputId" class="font-weight-bold">{{ label }}</label>
    <select
      :id="inputId"
      class="form-control"
      :value="modelValue"
      :disabled="disabled"
      :required="required"
      @change="onChange"
    >
      <option v-if="includeEmpty" value="">{{ emptyLabel }}</option>
      <option v-for="account in accounts" :key="account.id || account.iban" :value="account.iban">
        {{ accountLabel(account) }}
      </option>
    </select>
  </div>
</template>

<script>
import { formatCurrency } from '../../services/errorUtils';

export default {
  name: 'AccountSelector',
  props: {
    accounts: {
      type: Array,
      default: () => [],
    },
    modelValue: {
      type: String,
      default: '',
    },
    label: {
      type: String,
      default: 'Account',
    },
    emptyLabel: {
      type: String,
      default: 'Selecteer een account',
    },
    includeEmpty: {
      type: Boolean,
      default: true,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    required: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['update:modelValue', 'account-selected'],
  data() {
    return {
      inputId: `account-selector-${Math.random().toString(36).slice(2)}`,
    };
  },
  methods: {
    accountLabel(account) {
      const type = account.accountType === 'CHECKING' ? 'Betaalrekening' : 'Spaarrekening';
      return `${account.iban} - ${type} - ${formatCurrency(account.balance)}`;
    },
    onChange(event) {
      const iban = event.target.value;
      const account = this.accounts.find((item) => item.iban === iban) || null;

      this.$emit('update:modelValue', iban);
      this.$emit('account-selected', account);
    },
  },
};
</script>
