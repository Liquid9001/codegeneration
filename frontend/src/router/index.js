import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/LoginView.vue'),
    meta: { public: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/RegisterView.vue'),
    meta: { public: true },
  },
  {
    path: '/welcome',
    name: 'Welcome',
    component: () => import('../views/WelcomeView.vue'),
    meta: { requiresAuth: true },
  },
  // Customer routes
  {
    path: '/dashboard',
    name: 'CustomerDashboard',
    component: () => import('../views/customer/CustomerDashboard.vue'),
    meta: { requiresAuth: true, requiresApproved: true, role: 'CUSTOMER' },
  },
  {
    path: '/accounts/:id',
    name: 'AccountDetail',
    component: () => import('../views/customer/AccountDetailView.vue'),
    meta: { requiresAuth: true, requiresApproved: true },
  },
  {
    path: '/transfer',
    name: 'Transfer',
    component: () => import('../views/customer/TransferView.vue'),
    meta: { requiresAuth: true, requiresApproved: true, role: 'CUSTOMER' },
  },
  {
    path: '/atm',
    name: 'ATM',
    component: () => import('../views/customer/ATMView.vue'),
    meta: { requiresAuth: true, requiresApproved: true, role: 'CUSTOMER' },
  },
  {
    path: '/transactions',
    name: 'TransactionHistory',
    component: () => import('../views/customer/TransactionHistoryView.vue'),
    meta: { requiresAuth: true, requiresApproved: true, role: 'CUSTOMER' },
  },
  // Employee routes
  {
    path: '/employee',
    name: 'EmployeeDashboard',
    component: () => import('../views/employee/EmployeeDashboard.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYEE' },
  },
  {
    path: '/employee/customers',
    name: 'AllCustomers',
    component: () => import('../views/employee/AllCustomersView.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYEE' },
  },
  {
    path: '/employee/customers/pending',
    name: 'PendingCustomers',
    component: () => import('../views/employee/PendingCustomersView.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYEE' },
  },
  {
    path: '/employee/customers/:id',
    name: 'CustomerDetail',
    component: () => import('../views/employee/CustomerDetailView.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYEE' },
  },
  {
    path: '/employee/transactions',
    name: 'AllTransactions',
    component: () => import('../views/employee/AllTransactionsView.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYEE' },
  },
  // Default redirect
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  if (to.meta.public) {
    if (auth.isAuthenticated) {
      return next(auth.isEmployee ? '/employee' : auth.isApproved ? '/dashboard' : '/welcome')
    }
    return next()
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return next('/login')
  }

  if (to.meta.role === 'EMPLOYEE' && !auth.isEmployee) {
    return next(auth.isApproved ? '/dashboard' : '/welcome')
  }

  if (to.meta.role === 'CUSTOMER' && auth.isEmployee) {
    return next('/employee')
  }

  if (to.meta.requiresApproved && !auth.isApproved) {
    return next('/welcome')
  }

  next()
})

export default router
