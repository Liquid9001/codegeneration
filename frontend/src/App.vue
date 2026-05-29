<template>
  <div id="app">
    <AppNavbar v-if="showNavbar" />
    <main class="flex-grow-1">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <AppToast />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppNavbar from './components/AppNavbar.vue'
import AppToast from './components/AppToast.vue'

const route = useRoute()
const noNavRoutes = ['Login', 'Register']
const showNavbar = computed(() => !noNavRoutes.includes(route.name))
</script>

<style>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
</style>
