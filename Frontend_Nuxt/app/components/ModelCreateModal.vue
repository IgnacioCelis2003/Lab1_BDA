<script setup lang="ts">
import { ref } from 'vue';
const props = defineProps({
  show: { type: Boolean, default: false }
});
const emit = defineEmits(['update:show', 'created']);

const nombreModelo = ref('');
const fabricante = ref('');
const capacidadCargaKg = ref<number | null>(null);
const autonomiaMinutos = ref<number | null>(null);
const submitting = ref(false);
const error = ref<string | null>(null);

function close() {
  emit('update:show', false);
}

async function submit() {
  error.value = null;
  if (!nombreModelo.value || !fabricante.value || capacidadCargaKg.value == null || autonomiaMinutos.value == null) {
    error.value = 'Todos los campos son obligatorios';
    return;
  }

  submitting.value = true;
  try {
    await $fetch('/api/modelos/crear', {
      method: 'POST',
      body: {
        nombreModelo: nombreModelo.value,
        fabricante: fabricante.value,
        capacidadCargaKg: capacidadCargaKg.value,
        autonomiaMinutos: autonomiaMinutos.value
      }
    });
    emit('created');
    close();
  } catch (err: any) {
    console.error('[ModelCreateModal] create error', err);
    error.value = err?.data?.message || err?.message || String(err);
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <div v-if="props.show" class="modal" style="position:fixed;inset:0;background:rgba(0,0,0,0.4);display:flex;align-items:center;justify-content:center;z-index:40">
    <div class="card" style="max-width:640px;width:min(95vw,640px);max-height:80vh;overflow:auto;padding:1rem;box-sizing:border-box;background:rgba(31,31,52,0.98);border-radius:8px;border:1px solid rgba(0,0,0,0.12);box-shadow:0 8px 24px rgba(0,0,0,0.2);">
      <header class="modal-header">
        <h3>Agregar Modelo</h3>
      </header>

      <form @submit.prevent="submit" class="stack">
        <label>
          Nombre del modelo
          <input v-model="nombreModelo" type="text" />
        </label>

        <label>
          Fabricante
          <input v-model="fabricante" type="text" />
        </label>

        <label>
          Capacidad (kg)
          <input v-model.number="capacidadCargaKg" type="number" step="0.1" />
        </label>

        <label>
          Autonomía (min)
          <input v-model.number="autonomiaMinutos" type="number" />
        </label>

        <section style="display:flex;gap:0.5rem;justify-content:flex-end">
          <button type="button" class="secondary" @click="close">Cancelar</button>
          <button type="submit" class="primary" :disabled="submitting">{{ submitting ? 'Guardando...' : 'Guardar' }}</button>
        </section>

        <p v-if="error" class="error">{{ error }}</p>
      </form>
    </div>
    <div class="modal-backdrop" @click="close"></div>
  </div>
</template>

<style scoped>

.error { color: #b00020; }

</style>
