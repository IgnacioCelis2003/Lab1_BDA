<script setup lang="ts">
import MisionTypeCreate from "~/components/MisionTypeCreate.vue";
import { ref } from "vue";

const showModal = ref(false);

const showEditModal = ref(false);
const selectedTipo = ref<any | null>(null);

const deletingId = ref<number | null>(null);

const {
  data: tipos,
  error,
  status,
  refresh,
} = await useFetch("http://localhost:8080/api/tipos-mision");

function onCreated() {
  refresh();
  showModal.value = false;
}

function openEdit(t: any) {
  selectedTipo.value = { ...t };
  showEditModal.value = true;
}

function onSaved() {
  refresh();
  showEditModal.value = false;
  selectedTipo.value = null;
}

async function deleteTipo(t: any) {
  const id = t?.idTipoMision;
  if (!id) return;

  const ok = confirm(`¿Seguro que quieres eliminar el tipo de misión #${id}?`);
  if (!ok) return;

  deletingId.value = id;

  try {
    await $fetch(`http://localhost:8080/api/tipos-mision/eliminar/${id}`, {
      method: "DELETE",
    });

    if (selectedTipo.value?.idTipoMision === id) {
      showEditModal.value = false;
      selectedTipo.value = null;
    }

    await refresh();
  } catch (e: any) {
    alert(e?.data?.message || e?.message || "Error al eliminar tipo de misión");
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
        <h2>Tipos de Misión</h2>
        <div style="display: flex; gap: 0.5rem">
          <NuxtLink to="/misiones" class="secondary">Volver</NuxtLink>
          <button class="contrast" @click="showModal = true">
            Agregar Tipo
          </button>
        </div>
      </div>
    </div>

    <MisionTypeCreate v-model:show="showModal" @created="onCreated" />

    <MisionTypeCreate
      v-model:show="showEditModal"
      :tipo="selectedTipo"
      @saved="onSaved"
    />

    <article v-if="status === 'pending'" aria-busy="true" />

    <article class="error" v-else-if="error">
      {{ error.statusMessage || error.message }}
    </article>

    <section v-else class="grid">
      <article v-for="t in tipos" :key="t.idTipoMision" class="card">
        <h3>{{ t.nombreTipo }}</h3>

        <p><strong>ID:</strong> {{ t.idTipoMision }}</p>

        <div style="display: flex; gap: 0.5rem; margin-top: 0.75rem">
          <button class="secondary" @click="openEdit(t)">Editar</button>

          <button
            class="contrast"
            :disabled="deletingId === t.idTipoMision"
            :aria-busy="deletingId === t.idTipoMision"
            @click="deleteTipo(t)"
          >
            {{ deletingId === t.idTipoMision ? "Eliminando..." : "Eliminar" }}
          </button>
        </div>
      </article>
    </section>
  </main>
</template>
