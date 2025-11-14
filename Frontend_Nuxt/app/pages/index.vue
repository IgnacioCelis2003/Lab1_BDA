<script lang="ts" setup>

const { data: movies, error, status} = await useFetch('/api/movie', {
    lazy: true,
});
</script>

<template>
    <main class="container">
        <h2>Panel</h2>

        <div class="grid" style="display:flex;gap:1rem;justify-content:center;flex-wrap:wrap;align-items:stretch">
            <NuxtLink to="/drones" class="card">
                <h3>Drones</h3>
                <p>Ver y gestionar los drones registrados.</p>
            </NuxtLink>

            <NuxtLink to="/misiones" class="card">
                <h3>Misiones</h3>
                <p>Listado de misiones y su estado.</p>
            </NuxtLink>

            <NuxtLink to="/misiones" class="card">
                <h3>Resumen de Misiones</h3>
                <p>Estadísticas y resúmenes por tipo de misión.</p>
            </NuxtLink>
        </div>

            <!-- Imagen centrada debajo del grid -->
            <div style="display:flex;justify-content:center;margin-top:1.5rem;">
                <img
                    src="https://media.tenor.com/bHqLgorwltUAAAAe/juan-carlos-bodoque-bike.png"
                    alt="Juan Carlos Bodoque en bicicleta"
                    style="max-width:420px;width:100%;height:auto;display:block;"
                />
            </div>

        </main>
</template>