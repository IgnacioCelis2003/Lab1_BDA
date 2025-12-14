<script lang="ts" setup>
import { ref } from "vue";

// Interface para Misión
interface Mision {
  idMision: number;
  idDronAsignado: number | null;
  idTipoMision: number;
  idOperadorCreador: number;
  fechaCreacion: string;
  fechaInicioPlanificada: string;
  fechaFinPlanificada: string;
  fechaInicioReal: string | null;
  fechaFinReal: string | null;
  estado: string;
  rutaWKT: string;
}

// Obtener todas las misiones registradas
const {
  data: misiones,
  error,
  status,
  refresh,
} = await useFetch<Mision[]>(`/api/misiones/all`);

// Mostrar o no el modal de 'crear misión'
const showModal = ref(false);

// Manejo del evento de crear misión
async function onCreated() {
  showModal.value = false;
  await refresh();
}
</script>

<template>
  <main class="container">
    <div class="stack">
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
        "
      >
        <h2>Misiones</h2>
        <div style="display: flex; gap: 0.5rem">
          <NuxtLink to="/tiposmisiones" class="secondary">
            Revisar misiones disponibles
          </NuxtLink>
          <button class="contrast" @click="showModal = true">
            Crear misión
          </button>
        </div>
      </div>
    </div>

    <article v-if="status === 'pending'" aria-busy="true" />

    <article class="error" v-else-if="error">
      {{ error.statusMessage || error.message }}
    </article>

    <section v-else class="grid">
      <article v-for="m in misiones" :key="m.idMision" class="card">
        <h3>Misión #{{ m.idMision }}</h3>
        <p><strong>Tipo de Misión:</strong> ID {{ m.idTipoMision }}</p>
        <p>
          <strong>Dron Asignado:</strong>
          {{ m.idDronAsignado ? `ID ${m.idDronAsignado}` : "No asignado" }}
        </p>
        <p><strong>Estado:</strong> {{ m.estado }}</p>
        <hr style="margin: 0.5rem 0" />
        <p>
          <strong>Fecha Inicio Planificada:</strong>
          {{ new Date(m.fechaInicioPlanificada).toLocaleString("es-CL") }}
        </p>
        <p>
          <strong>Fecha Fin Planificada:</strong>
          {{ new Date(m.fechaFinPlanificada).toLocaleString("es-CL") }}
        </p>
        <p v-if="m.fechaInicioReal">
          <strong>Fecha Inicio Real:</strong>
          {{ new Date(m.fechaInicioReal).toLocaleString("es-CL") }}
        </p>
        <p v-if="m.fechaFinReal">
          <strong>Fecha Fin Real:</strong>
          {{ new Date(m.fechaFinReal).toLocaleString("es-CL") }}
        </p>
        <hr style="margin: 0.5rem 0" />
        <p>
          <strong>Ruta WKT:</strong>
          <code style="font-size: 0.85rem; word-break: break-word">{{
            m.rutaWKT
          }}</code>
        </p>
        <p>
          <strong>Creada:</strong>
          {{ new Date(m.fechaCreacion).toLocaleString("es-CL") }}
        </p>
      </article>
    </section>

    <!-- Componente Modal -->
    <MisionCreateModal v-model:show="showModal" @created="onCreated" />
  </main>
</template>
