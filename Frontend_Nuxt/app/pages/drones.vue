<script lang="ts" setup>
import { ref } from 'vue';

// Obtener todos los drones registrados
const { data: drones, error, status, refresh } = await useFetch('/api/drones/all');

// Mapear idModelo a Informacion del modelo
const modelsMap = ref<Record<number, any>>({});
const modelsLoading = ref(false);

// Mostrar o no el modal de 'crear un dron'
const showModal = ref(false);

// Funcion auxiliar para mostrar informacion de Modelo de los Drones cargados
async function loadModels() {
    modelsMap.value = {};
    if (drones.value && Array.isArray(drones.value) && drones.value.length > 0) {
        modelsLoading.value = true;
        const uniqueIds = Array.from(new Set(drones.value.map((d: any) => d.idModelo)));
        await Promise.all(uniqueIds.map(async (id: number) => {
            try {
                const m = await $fetch(`/api/modelos/${id}`);
                modelsMap.value[id] = m;
            } catch (e) {
                console.warn('[drones] failed fetching model', id, e);
            }
        }));
        modelsLoading.value = false;
    }
}

// Cargar modelos al inicar
await loadModels();

// Manejo del evento de mostrar modal
async function onCreated() {
    showModal.value = false;
    await refresh();
    await loadModels();
}
</script>

<template>
    <main class="container">

        <div class="stack">
            <div style="display:flex;justify-content:space-between;align-items:center">
                <h2>Drones</h2>
                                <div style="display:flex;gap:0.5rem">
                                    <NuxtLink to="/modelosdron" class="secondary">Revisar modelos disponibles</NuxtLink>
                                    <button class="contrast" @click="showModal = true">Agregar Dron</button>
                                </div>
            </div>
        </div>

        <article v-if="status === 'pending' || modelsLoading" aria-busy="true" />

        <article class="error" v-else-if="error">
            {{ error.statusMessage || error.message }}
        </article>

        <section v-else class="grid">
            <article v-for="d in drones" :key="d.idDron" class="card">
                <h3>Dron #{{ d.idDron }}</h3>
                <p><strong>Modelo ID:</strong> {{ d.idModelo }}</p>
                <p v-if="modelsMap[d.idModelo]"><strong>Modelo:</strong> {{ modelsMap[d.idModelo].nombreModelo }}</p>
                <p v-else><em>Información del modelo cargando...</em></p>
                <p><strong>Estado:</strong> {{ d.estado }}</p>
                <div v-if="modelsMap[d.idModelo]">
                    <p><strong>Fabricante:</strong> {{ modelsMap[d.idModelo].fabricante }}</p>
                    <p><strong>Capacidad (kg):</strong> {{ modelsMap[d.idModelo].capacidadCargaKg }}</p>
                    <p><strong>Autonomía (min):</strong> {{ modelsMap[d.idModelo].autonomiaMinutos }}</p>
                </div>
            </article>
        </section>

        <!-- Componente Modal -->
        <DroneCreateModal v-model:show="showModal" @created="onCreated" />

    </main>
</template>