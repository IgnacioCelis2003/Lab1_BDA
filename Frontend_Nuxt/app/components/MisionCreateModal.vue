<template>
  <div
    v-if="props.show"
    class="modal"
    style="
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 40;
    "
  >
    <div
      class="card"
      style="
        max-width: 640px;
        width: min(95vw, 640px);
        max-height: 80vh;
        overflow: auto;
        padding: 1rem;
        box-sizing: border-box;
        background: rgba(31, 31, 52, 0.98);
        border-radius: 8px;
        border: 1px solid rgba(0, 0, 0, 0.12);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
      "
    >
      <h3>Nueva Misión</h3>

      <form @submit.prevent="createMision">
        <!-- Tipo de misión -->
        <label>
          Tipo de Misión
          <select v-model.number="newMision.idTipoMision" required>
            <option v-if="!tiposMision && !tiposError" disabled>
              Cargando tipos...
            </option>
            <option v-if="tiposError" disabled>Error cargando tipos</option>
            <option
              v-for="t in tiposMision"
              :key="t.idTipoMision"
              :value="t.idTipoMision"
            >
              {{ t.nombreTipo }}
            </option>
          </select>
        </label>

        <!-- Dron asignado -->
        <label>
          Dron Asignado
          <select v-model.number="newMision.idDronAsignado" required>
            <option v-if="!drones && !dronesError" disabled>
              Cargando drones...
            </option>
            <option v-if="dronesError" disabled>Error cargando drones</option>
            <option v-for="d in drones" :key="d.idDron" :value="d.idDron">
              Dron {{ d.idDron }} (Modelo
              {{ modelsMap[d.idModelo]?.nombreModelo }})
            </option>
          </select>
        </label>

        <!-- Fechas -->
        <label>
          Fecha Inicio Planificada
          <input
            v-model="newMision.fechaInicioPlanificada"
            type="datetime-local"
            required
          />
        </label>

        <label>
          Fecha Fin Planificada
          <input
            v-model="newMision.fechaFinPlanificada"
            type="datetime-local"
            required
          />
        </label>

        <!-- Estado -->
        <label>
          Estado
          <select v-model="newMision.estado" required>
            <option>Pendiente</option>
            <option>Completada</option>
          </select>
        </label>

        <!-- Ruta -->
        <label>
          Ruta (selecciona 2 puntos en el mapa)
          <MapPicker mode="route" v-model:rutaWKT="newMision.rutaWKT" />

          <div style="margin-top: 0.5rem">
            <small>Ruta WKT generada:</small>
            <div
              style="
                font-size: 0.85rem;
                word-break: break-word;
                background: #0f1724;
                padding: 0.5rem;
                border-radius: 4px;
                margin-top: 0.25rem;
              "
            >
              {{
                newMision.rutaWKT || "Sin ruta (haz doble clic para limpiar)"
              }}
            </div>
          </div>
        </label>

        <!-- Acciones -->
        <div
          style="
            display: flex;
            gap: 0.5rem;
            justify-content: flex-end;
            margin-top: 1rem;
          "
        >
          <button type="button" class="secondary" @click="cancel">
            Cancelar
          </button>
          <button type="submit" :disabled="saving" class="contrast">
            {{ saving ? "Guardando..." : "Crear" }}
          </button>
        </div>

        <p v-if="formError" class="error" style="color: var(--error)">
          {{ formError }}
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import MapPicker from "~/components/MapPicker.vue";

const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:show", "created"]);

const saving = ref(false);
const formError = ref<string | null>(null);

// Interfaces
interface TipoMision {
  idTipoMision: number;
  nombreTipo: string;
}

interface Dron {
  idDron: number;
  nombreDron: string;
  idModelo: number;
}

// Fetch tipos de misión
const { data: tiposMision, error: tiposError } = await useFetch<TipoMision[]>(
  "/api/misiones/tipos/all"
);

// Fetch drones disponibles
const { data: drones, error: dronesError } = await useFetch<Dron[]>(
  "/api/drones/all"
);

// Mapear idModelo a información del modelo
const modelsMap = ref<Record<number, any>>({});
const modelsLoading = ref(false);

// Estado del formulario
const newMision = reactive({
  idTipoMision: 0,
  idDronAsignado: null as number | null,
  fechaInicioPlanificada: "",
  fechaFinPlanificada: "",
  estado: "Pendiente",
  rutaWKT: "",
});

// Seleccionar primer tipo de misión automáticamente
watchEffect(() => {
  if (
    tiposMision.value &&
    tiposMision.value.length &&
    newMision.idTipoMision === 0
  ) {
    newMision.idTipoMision = tiposMision.value[0].idTipoMision;
  }
});

// Cargar modelos de drones
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

// Cargar modelos al iniciar
await loadModels();

function cancel() {
  emit("update:show", false);
}

async function createMision() {
  formError.value = null;

  // Validaciones
  if (!newMision.idTipoMision) {
    formError.value = "Debes seleccionar un tipo de misión";
    return;
  }

  if (!newMision.fechaInicioPlanificada) {
    formError.value = "Debes ingresar una fecha de inicio";
    return;
  }

  if (!newMision.fechaFinPlanificada) {
    formError.value = "Debes ingresar una fecha de fin";
    return;
  }

  if (
    !newMision.rutaWKT ||
    !newMision.rutaWKT.toUpperCase().startsWith("LINESTRING")
  ) {
    formError.value =
      "Debes ingresar una ruta válida en formato WKT (LINESTRING)";
    return;
  }

  const payload = {
    idTipoMision: newMision.idTipoMision,
    idDronAsignado: newMision.idDronAsignado,
    fechaInicioPlanificada: new Date(
      newMision.fechaInicioPlanificada
    ).toISOString(),
    fechaFinPlanificada: new Date(newMision.fechaFinPlanificada).toISOString(),
    estado: newMision.estado,
    rutaWKT: newMision.rutaWKT,
  };

  saving.value = true;

  try {
    await $fetch("/api/misiones/crear", {
      method: "POST",
      body: payload,
    });

    emit("created");
    emit("update:show", false);
  } catch (e: any) {
    formError.value = e?.data?.message || e?.message || "Error al crear misión";
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
@media (max-width: 420px) {
  .card {
    padding: 0.75rem;
  }
}
</style>
