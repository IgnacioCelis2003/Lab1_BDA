<template>
  <article class="login-card">
    
    <hgroup>
      <h3>Iniciar Sesión</h3>
      <p>Ingresa tus credenciales para continuar</p>
    </hgroup>

    <form @submit.prevent="submitForm">
      
        <label for="nombre">
        Nombre
        <input 
          type="text" 
          id="name" 
          name="name" 
          placeholder="Nombre" 
          v-model="form.nombre"
          required 
        />
      </label>

      <label for="email">
        Correo Electrónico
        <input 
          type="email" 
          id="email" 
          name="email" 
          placeholder="tu@correo.com" 
          v-model="form.email"
          required 
        />
      </label>

      <label for="password">
        Contraseña
        <input 
          type="password" 
          id="password" 
          name="password" 
          placeholder="Tu contraseña" 
          v-model="form.password"
          required 
        />
      </label>


      <button 
        type="submit" 
        class="contrast"
      >
        Ingresar
      </button>
        <div v-if="error">{{ error }}</div>
    </form>

    
  </article>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'login',
})

const error = ref<string | null>(null);

async function submitForm(){

  const { data, error } = await useFetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      
      // El 'body' se convertirá automáticamente a JSON
      body: form,
      
      // No usamos `await` en la misma línea para poder manejar 'pending'
      // 'lazy: true' significa que no bloqueará la navegación (útil en otros casos)
      lazy: true,
    });

  return data;
}

const form = reactive({
  nombre: '',  
  email: '',
  password: ''
})

</script>

<style scoped>
/* Aunque Pico hace la mayoría del trabajo, podemos añadir estilos 
  específicos para centrar o limitar el ancho de nuestro componente.
*/
.login-card {
  max-width: 480px; /* Limita el ancho del formulario */
  margin: 4rem auto; /* Centra el formulario vertical y horizontalmente */
  padding: 2rem;
}

/* El spinner de Pico es un poco grande, lo ajustamos */
button[aria-busy="true"] {
  padding-left: 3rem;
  padding-right: 3rem;
}
</style>