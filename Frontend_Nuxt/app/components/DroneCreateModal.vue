<template>
  <div v-if="props.show" class="modal" style="position:fixed;inset:0;background:rgba(0,0,0,0.4);display:flex;align-items:center;justify-content:center;z-index:40">
    <div class="card" style="max-width:640px;width:min(95vw,640px);max-height:80vh;overflow:auto;padding:1rem;box-sizing:border-box;background:rgba(31,31,52,0.98);border-radius:8px;border:1px solid rgba(0,0,0,0.12);box-shadow:0 8px 24px rgba(0,0,0,0.2);">
      <h3>Nuevo Dron</h3>
      <form @submit.prevent="createDrone">
        <label>
          Placa
          <input v-model="newDrone.placa" required />
        </label>

        <label>
          idModelo
          <input type="number" v-model.number="newDrone.idModelo" required />
        </label>

        <label>
          Estado
          <select v-model="newDrone.estado">
            <option>Disponible</option>
            <option>En Mantenimiento</option>
          </select>
        </label>

        <label>
          Horas de vuelo
          <input type="number" step="0.1" v-model.number="newDrone.horasVuelo" />
        </label>

        <label>
          Última fecha de vuelo
          <input type="datetime-local" v-model="newDrone.ultimaFechaVuelo" />
        </label>

        <label>
          Ubicación Lat
          <input type="number" step="0.000001" v-model.number="newDrone.ubicacionLat" />
        </label>

        <label>
          Ubicación Lon
          <input type="number" step="0.000001" v-model.number="newDrone.ubicacionLon" />
        </label>

        <label>
          Capacidad carga (kg)
          <input type="number" step="0.1" v-model.number="newDrone.capacidadCargaKg" />
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
import { reactive, ref } from 'vue';

const props = defineProps({ show: { type: Boolean, default: false } });
const emit = defineEmits(['update:show', 'created']);

const saving = ref(false);
const formError = ref<string | null>(null);

const newDrone = reactive({
  placa: '',
  idModelo: 1,
  estado: 'Disponible',
  horasVuelo: 0,
  ultimaFechaVuelo: new Date().toISOString(),
  ubicacionLat: 0.0,
  ubicacionLon: 0.0,
  capacidadCargaKg: 0.0
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
    try { payload.ultimaFechaVuelo = new Date(newDrone.ultimaFechaVuelo).toISOString(); } catch (e) {}
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
