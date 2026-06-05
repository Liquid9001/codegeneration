export const getErrorMessage = (error, fallback = 'Er is een fout opgetreden.') => {
  const body = error?.response?.data;

  if (body?.message) {
    return body.message;
  }

  if (typeof body === 'string') {
    return body;
  }

  if (body?.error && body.error !== 'Bad Request' && body.error !== 'Unauthorized') {
    return body.error;
  }

  return error?.message || fallback;
};

export const formatCurrency = (value) => {
  const amount = Number(value || 0);

  return new Intl.NumberFormat('nl-NL', {
    style: 'currency',
    currency: 'EUR',
  }).format(amount);
};

export const formatDateTime = (value) => {
  if (!value) {
    return '-';
  }

  return new Intl.DateTimeFormat('nl-NL', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value));
};
