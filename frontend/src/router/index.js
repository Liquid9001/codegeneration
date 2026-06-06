import { createRouter, createWebHashHistory } from 'vue-router';
import Login from '../components/Login.vue';
import Dashboard from '../components/Dashboard.vue';
import Users from '../components/admin/Users.vue'; 
import Register from '../components/Register.vue'; 
import ApproveUser from '../components/admin/ApproveUser.vue';
import BankAccounts from '../components/admin/BankAccounts.vue';
import AccountDetails from '../components/admin/AccountDetails.vue';
import TransferPage from '../components/transactions/TransferPage.vue';
import AtmPage from '../components/transactions/AtmPage.vue';
import TransactionHistoryPage from '../components/transactions/TransactionHistoryPage.vue';
import { useAuthStore } from '../store/auth';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { publicOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { publicOnly: true }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/transfer',
    name: 'Transfer',
    component: TransferPage,
    meta: { requiresAuth: true, roles: ['CUSTOMER'] }
  },
  {
    path: '/atm',
    name: 'Atm',
    component: AtmPage
  },
  {
    path: '/transactions',
    name: 'TransactionHistory',
    component: TransactionHistoryPage,
    meta: { requiresAuth: true, roles: ['CUSTOMER', 'EMPLOYEE', 'ADMIN'] }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: Users,
    meta: { requiresAuth: true, roles: ['ADMIN', 'EMPLOYEE'] }
  },
  {
    path: '/admin/bankaccounts',
    name: 'BankAccounts',
    component: BankAccounts,
    meta: { requiresAuth: true, roles: ['ADMIN', 'EMPLOYEE'] }
  },
  {
    path: '/admin/users/:id/approve',
    name: 'ApproveUser',
    component: ApproveUser,
    meta: { requiresAuth: true, roles: ['ADMIN', 'EMPLOYEE'] }
  },
  {
    path: '/admin/accounts/:accountId',
    name: 'AccountDetails',
    component: AccountDetails,
    meta: { requiresAuth: true, roles: ['ADMIN', 'EMPLOYEE'] }
  },
];

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.ensureValidSession();

  if (to.matched.some(record => record.meta.publicOnly) && isAuthenticated) {
    next({ name: 'Dashboard' });
    return;
  }

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    next({ name: 'Login' });
    return;
  }

  const allowedRoles = to.matched
    .flatMap(record => record.meta.roles || []);

  if (allowedRoles.length > 0 && !allowedRoles.includes(authStore.userRole)) {
    next({ name: 'Dashboard' });
    return;
  }

  next();
});

export default router;
