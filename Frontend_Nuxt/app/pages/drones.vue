<script lang="ts" setup>
import { ref } from "vue";

const {
  data: drones,
  error,
  status,
  refresh,
} = await useFetch("/api/drones/all");

// Mapear idModelo a información del modelo
const modelsMap = ref<Record<number, any>>({});
const modelsLoading = ref(false);

// Modal crear
const showModal = ref(false);

// Modal editar
const showEditModal = ref(false);
const selectedDron = ref<any | null>(null);

// Estado para botón eliminar
const deletingId = ref<number | null>(null);

async function loadModels() {
  modelsMap.value = {};

  if (drones.value && Array.isArray(drones.value) && drones.value.length > 0) {
    modelsLoading.value = true;

    const uniqueIds = Array.from(
      new Set(drones.value.map((d: any) => d.idModelo))
    );

    await Promise.all(
      uniqueIds.map(async (id: number) => {
        try {
          const m = await $fetch(`/api/modelos/${id}`);
          modelsMap.value[id] = m;
        } catch (e) {
          console.warn("[drones] failed fetching model", id, e);
        }
      })
    );

    modelsLoading.value = false;
  }
}

await loadModels();

async function onCreated() {
  showModal.value = false;
  await refresh();
  await loadModels();
}

function openEdit(d: any) {
  selectedDron.value = { ...d };
  showEditModal.value = true;
}

async function onSaved() {
  showEditModal.value = false;
  selectedDron.value = null;
  await refresh();
  await loadModels();
}

// ✅ Eliminar dron
async function deleteDron(d: any) {
  const idDron = d?.idDron;
  if (!idDron) return;

  const ok = confirm(`¿Seguro que quieres eliminar el dron #${idDron}?`);
  if (!ok) return;

  deletingId.value = idDron;

  try {
    await $fetch(`http://localhost:8080/api/drones/eliminar/${idDron}`, {
      method: "DELETE",
    });

    // si justo estabas editando ese dron, cierra modal
    if (selectedDron.value?.idDron === idDron) {
      showEditModal.value = false;
      selectedDron.value = null;
    }

    await refresh();
    await loadModels();
  } catch (e: any) {
    alert(e?.data?.message || e?.message || "Error al eliminar dron");
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
        <h2>Drones</h2>
        <div style="display: flex; gap: 0.5rem">
          <NuxtLink to="/modelosdron" class="secondary">
            Revisar modelos disponibles
          </NuxtLink>
          <button class="contrast" @click="showModal = true">
            Agregar Dron
          </button>
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
        <p v-if="modelsMap[d.idModelo]">
          <strong>Modelo:</strong> {{ modelsMap[d.idModelo].nombreModelo }}
        </p>
        <p v-else><em>Información del modelo cargando...</em></p>

        <p><strong>Estado:</strong> {{ d.estado }}</p>

        <div v-if="modelsMap[d.idModelo]">
          <p>
            <strong>Fabricante:</strong> {{ modelsMap[d.idModelo].fabricante }}
          </p>
          <p>
            <strong>Capacidad (kg):</strong>
            {{ modelsMap[d.idModelo].capacidadCargaKg }}
          </p>
          <p>
            <strong>Autonomía (min):</strong>
            {{ modelsMap[d.idModelo].autonomiaMinutos }}
          </p>
        </div>

        <div style="display: flex; gap: 0.5rem; margin-top: 0.75rem">
          <button class="secondary" @click="openEdit(d)">Editar modelo</button>

          <button
            class="contrast"
            :disabled="deletingId === d.idDron"
            :aria-busy="deletingId === d.idDron"
            @click="deleteDron(d)"
          >
            {{ deletingId === d.idDron ? "Eliminando..." : "Eliminar" }}
          </button>
        </div>
      </article>
    </section>

    <!-- Modal crear -->
    <DroneFormModal v-model:show="showModal" @created="onCreated" />

    <!-- Modal editar -->
    <DroneFormModal
      v-model:show="showEditModal"
      :dron="selectedDron"
      @saved="onSaved"
    />
  </main>
</template>
