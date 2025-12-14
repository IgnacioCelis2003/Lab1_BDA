<script setup lang="ts">
import { ref } from "vue";

const showModal = ref(false);
const showEditModal = ref(false);
const selectedModel = ref<any | null>(null);
const deletingId = ref<number | null>(null);

const {
  data: modelos,
  error,
  status,
  refresh,
} = await useFetch("/api/modelos/all");

function onCreated() {
  refresh();
  showModal.value = false;
}

function openEdit(m: any) {
  selectedModel.value = { ...m };
  showEditModal.value = true;
}

function onSaved() {
  refresh();
  showEditModal.value = false;
  selectedModel.value = null;
}

async function deleteModelo(m: any) {
  const id = m?.idModelo;
  if (!id) return;

  const ok = confirm(`¿Seguro que quieres eliminar el modelo #${id}?`);
  if (!ok) return;

  deletingId.value = id;

  try {
    await $fetch(`/api/modelos/eliminar/${id}`, {
      method: 'DELETE'
    });
    await refresh();
  } catch (e) {
    console.error("Failed deleting model", e);
    alert("Error eliminando el modelo.");
  } finally {
    deletingId.value = null;
  }
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
        <h2>Modelos Disponibles</h2>
        <div style="display: flex; gap: 0.5rem">
          <NuxtLink to="/drones" class="secondary">Volver</NuxtLink>
          <button class="contrast" @click="showModal = true">
            Agregar Modelo
          </button>
        </div>
      </div>
    </div>

    <ModelCreateModal v-model:show="showModal" @created="onCreated" />

    <ModelCreateModal
      v-model:show="showEditModal"
      :modelo="selectedModel"
      @saved="onSaved"
    />

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

        <div style="display: flex; gap: 0.5rem; margin-top: 0.75rem">
          <button class="secondary" @click="openEdit(m)">Editar</button>

          <button
            class="contrast"
            :disabled="deletingId === m.idModelo"
            :aria-busy="deletingId === m.idModelo"
            @click="deleteModelo(m)"
          >
            {{ deletingId === m.idModelo ? "Eliminando..." : "Eliminar" }}
          </button>
        </div>
      </article>
    </section>
  </main>
</template>
