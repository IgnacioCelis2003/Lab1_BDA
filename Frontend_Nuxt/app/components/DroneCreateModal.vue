<template>
  <div v-if="props.show" class="modal" style="position:fixed;inset:0;background:rgba(0,0,0,0.4);display:flex;align-items:center;justify-content:center;z-index:40">
    <div class="card" style="max-width:640px;width:min(95vw,640px);max-height:80vh;overflow:auto;padding:1rem;box-sizing:border-box;background:rgba(31,31,52,0.98);border-radius:8px;border:1px solid rgba(0,0,0,0.12);box-shadow:0 8px 24px rgba(0,0,0,0.2);">
      <h3>Nuevo Dron</h3>
      <form @submit.prevent="createDrone">

        <label>
          Modelo
          <select v-model.number="newDrone.idModelo" required>
            <option v-if="!modelos && !modelosError" disabled> Cargando modelos... </option>
            <option v-if="modelosError" disabled> Error cargando modelos </option>
            <option v-for="m in modelos" :key="m.idModelo" :value="m.idModelo">{{ m.nombreModelo }} — {{ m.fabricante }}</option>
          </select>
        </label>

        <label>
          Estado
          <select v-model="newDrone.estado">
            <option>Disponible</option>
            <option>En Mantenimiento</option>
          </select>
        </label>

        <div style="display:flex;gap:0.5rem;justify-content:flex-end;margin-top:1rem">
          <button type="button" class="secondary" @click="cancel">Cancelar</button>
          <button type="submit" :disabled="saving" class="contrast">{{ saving ? 'Guardando...' : 'Crear' }}</button>
        </div>
        <p class="error" v-if="formError" style="color:var(--error)">{{ formError }}</p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from 'vue';
import MapPicker from '~/components/MapPicker.vue';

const props = defineProps({ show: { type: Boolean, default: false } });
const emit = defineEmits(['update:show', 'created']);

const saving = ref(false);
const formError = ref<string | null>(null);

// fetch available models from server
// fetch available models from server (typed)
interface Model {
  idModelo: number;
  nombreModelo: string;
  fabricante: string;
  capacidadCargaKg: number;
  autonomiaMinutos: number;
}

const { data: modelos, error: modelosError } = await useFetch<Model[]>('/api/modelos/all');

const newDrone = reactive({
  idModelo: 0,
  estado: 'Disponible',
});

// when modelos load, default to the first available id if not set
watchEffect(() => {
  if (modelos && modelos.value && modelos.value.length && (!newDrone.idModelo || newDrone.idModelo === 0)) {
    const first = modelos.value[0];
    if (first && first.idModelo) newDrone.idModelo = first.idModelo;
  }
});

function cancel() {
  emit('update:show', false);
}

async function createDrone() {
  formError.value = null;
  if (!['Disponible', 'En Mantenimiento'].includes(newDrone.estado)) {
    formError.value = 'El campo estado debe ser "Disponible" o "En Mantenimiento"';
    return;
  }
  saving.value = true;
  try {
    const payload: any = { ...newDrone };
    await $fetch('/api/drones/crear', { method: 'POST', body: payload });
    emit('created');
    emit('update:show', false);
  } catch (e: any) {
    formError.value = e?.data?.message || e?.message || 'Error al crear dron';
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
/* Slightly limit modal content width on very small screens */
@media (max-width: 420px) {
  .card { padding: 0.75rem; }
}
</style>
