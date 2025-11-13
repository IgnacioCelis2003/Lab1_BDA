<script setup lang="ts">
import { ref } from 'vue';

const showModal = ref(false);
const { data: modelos, error, status, refresh } = await useFetch('/api/modelos/all');

function onCreated() {
  // refresh the models list after a successful creation
  refresh();
  showModal.value = false;
}
</script>

<template>
  <main class="container">
    <div class="stack">
            <div style="display:flex;justify-content:space-between;align-items:center">
                <h2>Modelos Disponibles</h2>
                <div style="display:flex;gap:0.5rem">
                    <NuxtLink to="/drones" class="secondary">Volver</NuxtLink>
                  <button class="contrast" @click="showModal = true">Agregar Modelo</button>
                </div>
            </div>
        </div>

  <!-- Model create modal -->
  <ModelCreateModal v-model:show="showModal" @created="onCreated" />

    <article v-if="status === 'pending'" aria-busy="true" />

    <article class="error" v-else-if="error">
      {{ error.statusMessage || error.message }}
    </article>

    <section v-else class="grid">
      <article v-for="m in modelos" :key="m.idModelo" class="card">
        <h3>{{ m.nombreModelo }}</h3>
        <p><strong>Fabricante:</strong> {{ m.fabricante }}</p>
        <p><strong>Capacidad (kg):</strong> {{ m.capacidadCargaKg }}</p>
        <p><strong>Autonomía (min):</strong> {{ m.autonomiaMinutos }}</p>
      </article>
    </section>
  </main>
</template>
