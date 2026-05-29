<template>
  <div class="container">
    <nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
      <a class="navbar-brand" href="#">ELS Frontend</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ml-auto">
          <li v-if="!isLoggedIn" class="nav-item">
            <router-link :to="{ name: 'Login' }" class="nav-link">Login</router-link>
          </li>
          <li v-else class="nav-item">
            <button class="btn btn-link nav-link" @click="logout">Logout</button>
          </li>
        </ul>
      </div>
    </nav>

    <router-view></router-view>
  </div>
</template>

<script>
import { useAuthStore } from './store/auth';
import { computed } from 'vue';

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore();
    return {
      isLoggedIn: computed(() => authStore.isLoggedIn),
      logout: () => authStore.logout()
    };
  }
};
</script>

<style>
/* Add any global styles here */
</style>
