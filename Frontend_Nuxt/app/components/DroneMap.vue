<template>
  <div class="map-container">
    <div id="map"></div>
    <div v-if="drones.length === 0" class="overlay">
      No hay drones activos
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const map = ref(null);
const markers = ref({});
const drones = ref([]);
let L; // Leaflet import dinámico

// Crear icono según nivel de batería
function getDroneIcon(bateria) {
  let color;
  if (bateria > 50) color = 'green';
  else if (bateria > 20) color = 'orange';
  else color = 'red';

  return L.icon({
    iconUrl: `https://via.placeholder.com/30/${color}/000000?text=🚁`,
    iconSize: [30, 30],
    iconAnchor: [15, 30],
    popupAnchor: [0, -30],
  });
}

// Obtener drones y actualizar marcadores
async function fetchDrones() {
  try {
    // Usar $fetch en cliente
    const data = await $fetch('/api/telemetria/monitoreo');
    drones.value = data || [];

    drones.value.forEach(drone => {
      const icon = getDroneIcon(drone.bateria);

      if (markers.value[drone.id]) {
        // Actualizar posición y popup
        markers.value[drone.id].setLatLng([drone.latitud, drone.longitud]);
        markers.value[drone.id].setIcon(icon);
        markers.value[drone.id].bindPopup(`
          <b>Dron ${drone.id}</b><br>
          Batería: ${drone.bateria}%<br>
          Última actualización: ${new Date(drone.timestamp).toLocaleString()}
        `);
      } else {
        // Crear nuevo marcador
        const marker = L.marker([drone.latitud, drone.longitud], { icon }).addTo(map.value);
        marker.bindPopup(`
          <b>Dron ${drone.id}</b><br>
          Batería: ${drone.bateria}%<br>
          Última actualización: ${new Date(drone.timestamp).toLocaleString()}
        `);
        markers.value[drone.id] = marker;
      }
    });
  } catch (error) {
    console.error('Error al obtener drones:', error);
  }
}

onMounted(async () => {
  // Importar Leaflet solo en cliente
  L = await import('leaflet');

  // Inicializar mapa
  map.value = L.map('map').setView([-33.43, -70.64], 13);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map.value);

  // Primer fetch y actualización periódica
  await fetchDrones();
  // setInterval(fetchDrones, 5000);
});
</script>

<style scoped>
.map-container {
  position: relative;
  height: 500px;
  width: 100%;
}
#map {
  height: 100%;
  width: 100%;
}
.overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 255, 255, 0.85);
  padding: 1rem 2rem;
  border-radius: 8px;
  font-weight: bold;
  color: #333;
  text-align: center;
  pointer-events: none;
}
</style>
