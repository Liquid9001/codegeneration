import api from '../api';

const cleanParams = (params) => Object.fromEntries(
  Object.entries(params || {}).filter(([, value]) => value !== undefined && value !== null && value !== '')
);

const transactionPayload = ({
  senderIban,
  receiverIban,
  amount,
  description,
  initiatedByUserId,
}) => ({
  senderIban,
  receiverIban,
  initiatedByUserId,
  amount: Number(amount),
  currency: 'EUR',
  description: description?.trim() || undefined,
});

export const getTransactions = (params = {}) => api.get('/transactions', {
  params: cleanParams(params),
});

export const getTransactionById = (transactionId) => api.get(`/transactions/${transactionId}`);

export const transfer = (payload) => api.post('/transactions/transfer', transactionPayload(payload));

export const deposit = ({ receiverIban, amount, description, initiatedByUserId }) => api.post('/transactions/deposit', {
  receiverIban,
  initiatedByUserId,
  amount: Number(amount),
  currency: 'EUR',
  description: description?.trim() || undefined,
});

export const withdrawal = ({ senderIban, amount, description, initiatedByUserId }) => api.post('/transactions/withdrawal', {
  senderIban,
  initiatedByUserId,
  amount: Number(amount),
  currency: 'EUR',
  description: description?.trim() || undefined,
});
