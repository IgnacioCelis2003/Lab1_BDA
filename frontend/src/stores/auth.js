import { defineStore } from 'pinia';
import apiClient from '@/services/api';

export const useAuthStore = defineStore('auth', {
  // El 'state' es donde guardamos el token
  state: () => ({
    // Intentamos cargar el token desde localStorage al iniciar
    token: localStorage.getItem('token') || null,
  }),
  
  // Los 'actions' son los métodos que cambian el estado
  actions: {
    /**
     * Acción de Login: Llama a la API, obtiene el token y lo guarda.
     */
    async login(email, password) {
      try {
        // 1. Llamamos a tu API de login (¡la que probaste en Postman!)
        const response = await apiClient.post('/auth/login', {
          email: email,
          password: password
        });

        const token = response.data.token;
        
        // 2. Guardamos el token en nuestro estado (Pinia)
        this.token = token;
        
        // 3. ¡GUARDAMOS EN LOCALSTORAGE!
        // Esta es la respuesta a "cómo lo guardo en la caché"
        localStorage.setItem('token', token);
        
        return true;
      } catch (error) {
        console.error("Error en el login:", error);
        return false;
      }
    },
    
    /**
     * Acción de Logout: Borra el token de todos lados.
     */
    logout() {
      this.token = null;
      localStorage.removeItem('token');
    }
  },
  
  // Los 'getters' son para leer el estado
  getters: {
    isLoggedIn: (state) => !!state.token // Devuelve 'true' si hay un token
  }
});