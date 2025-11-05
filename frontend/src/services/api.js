import axios from 'axios';


const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', 
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
  (config) => {
    // 1. Obtenemos el store de autenticación
    const authStore = useAuthStore();
    const token = authStore.token;

    // 2. Si el token existe (estamos logueados)
    if (token) {
      // 3. Añadimos la cabecera 'Authorization'
      // Esto es lo que tu JwtRequestFilter en Spring Boot está esperando
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    return config; // Dejamos que la petición continúe
  },
  (error) => {
    return Promise.reject(error);
  }
);


export default apiClient;