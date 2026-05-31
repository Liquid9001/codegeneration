import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import Dashboard from '../components/Dashboard.vue';
import Users from '../components/admin/Users.vue'; 
import Register from '../components/Register.vue'; 
import ApproveUser from '../components/admin/ApproveUser.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: Users,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/users/:id/approve',
    name: 'ApproveUser',
    component: ApproveUser,
    meta: { requiresAuth: true }
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({ name: 'Login' });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
