export default defineNuxtRouteMiddleware(async (to, from) => {
  // Allow navigate to login/register freely
  if (to.path.startsWith('/login') || to.path.startsWith('/register')) return;

  try {
    const { data } = await useFetch('/api/auth/check');
    if (!data.value?.authenticated) {
      return navigateTo('/login');
    }
  } catch (e) {
    return navigateTo('/login');
  }
});
