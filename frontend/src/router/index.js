import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [],
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  // Si la ruta (to.meta.requiresAuth) requiere login Y no estamos logueados
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login'); // Lo redirigimos al login
  } else {
    next(); // Lo dejamos pasar
  }
});

export default router
