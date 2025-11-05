package com.Lab1BDA.Backend.controller;

import com.Lab1BDA.Backend.dto.*;
import com.Lab1BDA.Backend.model.Mision;
import com.Lab1BDA.Backend.service.MisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // ¡Importante!
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para la gestión de Misiones.
 * Todos los endpoints aquí están protegidos por JWT (definido en SecurityConfig).
 */
@RestController
@RequestMapping("/api/misiones")
public class MisionController {

    @Autowired
    private MisionService misionService;

    /**
     * Endpoint para obtener todas las misiones.
     * GET /api/misiones
     */
    @GetMapping
    public ResponseEntity<List<Mision>> listarTodasLasMisiones() {
        return ResponseEntity.ok(misionService.getTodasLasMisiones());
    }

    /**
     * Endpoint para obtener una misión por su ID.
     * GET /api/misiones/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mision> obtenerMisionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(misionService.getMisionPorId(id));
    }

    /**
     * Endpoint para crear una nueva misión.
     * POST /api/misiones
     * @param misionDTO Los datos de la misión (desde el cuerpo JSON).
     * @param authentication Spring Security inyecta esto automáticamente
     * con los datos del usuario autenticado (del token).
     * @return La misión creada (con HTTP 201 Created).
     */
    @PostMapping("/crear")
    public ResponseEntity<Mision> crearMision(@RequestBody MisionRequestDTO misionDTO,
                                              Authentication authentication) {

        // Obtenemos el email (que es el "username" en nuestro JwtTokenProvider)
        // del usuario que está llamando a este endpoint.
        String emailCreador = authentication.getName();

        Mision misionCreada = misionService.crearMision(misionDTO, emailCreador);

        // Devolvemos un 201 Created con la ubicación del nuevo recurso
        URI location = URI.create("/api/misiones/" + misionCreada.getIdMision());
        return ResponseEntity.created(location).body(misionCreada);
    }

    /**
     * Endpoint para actualizar una misión existente.
     * PUT /api/misiones/{id}
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Mision> actualizarMision(@PathVariable Long id,
                                                   @RequestBody MisionRequestDTO misionDTO) {
        Mision misionActualizada = misionService.actualizarMision(id, misionDTO);
        return ResponseEntity.ok(misionActualizada);
    }

    /**
     * Endpoint para eliminar una misión.
     * DELETE /api/misiones/{id}
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarMision(@PathVariable Long id) {
        misionService.eliminarMision(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    /**
     * Endpoint para asignar una misión existente a un dron específico.
     * Llama al procedimiento almacenado.
     * Se accede vía POST /api/misiones/1/asignar/5
     * * @param idMision El ID de la misión (desde la URL).
     * @param idDron El ID del dron (desde la URL).
     * @return ResponseEntity con estado 200 (OK) si tiene éxito.
     */
    @PostMapping("/{idMision}/asignar/{idDron}")
    public ResponseEntity<Void> asignarMision(
            @PathVariable Long idMision,
            @PathVariable Long idDron) {

        // Simplemente llamamos al servicio.
        // Si el servicio (o el SP) lanza una excepción,
        // Spring la manejará y devolverá un error (ej. 500 o 400).
        misionService.asignarMisionADron(idMision, idDron);

        // Si la llamada tiene éxito, devolvemos 200 OK.
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para el Reporte #3: Desempeño por Tipo de Misión.
     * Compara misiones completadas por tipo para los 2 modelos de dron más usados.
     * Se accede vía GET /api/misiones/reportes/desempeno-tipo
     */
    @GetMapping("/reportes/desempeno-tipo")
    public ResponseEntity<List<DesempenoTipoMisionDTO>> getReporteDesempenoTipoMision() {
        List<DesempenoTipoMisionDTO> reporte = misionService.getReporteDesempenoTipoMision();
        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para el Reporte #4: Patrones de Consumo de Batería.
     * Se accede vía GET /api/misiones/reportes/consumo-bateria
     */
    @GetMapping("/reportes/consumo-bateria")
    public ResponseEntity<List<BateriaConsumoDTO>> getReporteConsumoBateria() {
        List<BateriaConsumoDTO> reporte = misionService.getReporteConsumoBateria();
        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para el Reporte #5: Análisis de Desempeño Mensual.
     * Se accede vía GET /api/misiones/reportes/desempeno-mensual
     */
    @GetMapping("/reportes/desempeno-mensual")
    public ResponseEntity<List<DesempenoMensualDTO>> getReporteDesempenoMensual() {
        List<DesempenoMensualDTO> reporte = misionService.getReporteDesempenoMensual();
        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para el Reporte #10: Resumen de Misiones por Tipo (Vista Materializada).
     * Se accede vía GET /api/misiones/reportes/resumen-tipo
     */
    @GetMapping("/reportes/resumen-tipo")
    public ResponseEntity<List<ResumenMisionTipoDTO>> getReporteResumenMisiones() {
        List<ResumenMisionTipoDTO> reporte = misionService.getReporteResumenMisiones();
        return ResponseEntity.ok(reporte);
    }

}