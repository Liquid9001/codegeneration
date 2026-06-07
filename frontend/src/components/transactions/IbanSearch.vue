<template>
  <div class="iban-search">
    <div class="form-row">
      <div class="form-group col-md-5 text-left">
        <label for="iban-search-first-name" class="font-weight-bold">Voornaam</label>
        <input
          id="iban-search-first-name"
          v-model.trim="firstName"
          type="text"
          class="form-control"
          placeholder="Jane"
          :disabled="loading"
        />
      </div>
      <div class="form-group col-md-5 text-left">
        <label for="iban-search-last-name" class="font-weight-bold">Achternaam</label>
        <input
          id="iban-search-last-name"
          v-model.trim="lastName"
          type="text"
          class="form-control"
          placeholder="Smith"
          :disabled="loading"
        />
      </div>
      <div class="form-group col-md-2 d-flex align-items-end">
        <button type="button" class="btn btn-outline-primary btn-block" :disabled="loading" @click="search">
          {{ loading ? 'Zoeken...' : 'Zoeken' }}
        </button>
      </div>
    </div>

    <div v-if="error" class="alert alert-danger py-2 text-left">
      {{ error }}
    </div>

    <div v-if="searched && !loading && results.length === 0 && !error" class="alert alert-info py-2 text-left">
      Geen klanten gevonden voor deze naam.
    </div>

    <div v-if="results.length > 0" class="table-responsive">
      <table class="table table-sm table-hover bg-white mb-0">
        <thead>
          <tr>
            <th>Naam</th>
            <th>IBAN</th>
            <th class="text-right">Actie</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="result in results"
            :key="`${result.customerId}-${result.iban}`"
            :class="{ 'table-primary': selectedIban === result.iban }"
          >
            <td>{{ result.firstName }} {{ result.lastName }}</td>
            <td class="text-monospace">{{ result.iban }}</td>
            <td class="text-right">
              <button type="button" class="btn btn-sm btn-primary" @click="$emit('selected', result)">
                {{ selectedIban === result.iban ? 'Geselecteerd' : 'Selecteer' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { searchCustomers } from '../../services/customerService';
import { getErrorMessage } from '../../services/errorUtils';

export default {
  name: 'IbanSearch',
  props: {
    selectedIban: {
      type: String,
      default: '',
    },
  },
  emits: ['selected', 'search-start'],
  data() {
    return {
      firstName: '',
      lastName: '',
      results: [],
      loading: false,
      error: '',
      searched: false,
    };
  },
  methods: {
    async search() {
      this.error = '';
      this.results = [];
      this.searched = false;

      if (!this.firstName || !this.lastName) {
        this.error = 'Vul een voornaam en achternaam in.';
        return;
      }

      this.$emit('search-start');
      this.loading = true;

      try {
        const response = await searchCustomers(this.firstName, this.lastName);
        this.results = response.data || [];
        this.searched = true;
      } catch (error) {
        this.error = getErrorMessage(error, 'Klant zoeken is mislukt.');
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
