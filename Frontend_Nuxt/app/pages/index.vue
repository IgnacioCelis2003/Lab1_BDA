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

    </main>
</template>