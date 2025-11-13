export default defineNuxtRouteMiddleware(async (to, from) => {
  // Allow free access to login, register and public assets
  const publicPaths = ['/login', '/register'];
  if (publicPaths.some(p => to.path.startsWith(p))) return;

  try {
    // Use $fetch for a simple, immediate call to our server check endpoint
    const res = await $fetch('/api/auth/check');
    if (!res || !res.authenticated) {
      return navigateTo('/login');
    }
  } catch (e) {
    // If anything fails, redirect to login
    return navigateTo('/login');
  }
});
