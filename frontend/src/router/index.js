import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import TransactionsView from '../views/TransactionsView.vue'
import TransferView from '../views/TransferView.vue'
import EmployeeView from '../views/EmployeeView.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    component: LoginView
  },
  {
    path: '/register',
    component: RegisterView
  },
  {
    path: '/dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },
  {
    path: '/transactions',
    component: TransactionsView,
    meta: { requiresAuth: true }
  },
  {
    path: '/transfer',
    component: TransferView,
    meta: { requiresAuth: true }
  },
  {
    path: '/employee',
    component: EmployeeView,
    meta: {
      requiresAuth: true,
      employeeOnly: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  if (to.meta.employeeOnly && user?.role !== 'EMPLOYEE') {
    return next('/dashboard')
  }

  next()
})

export default router