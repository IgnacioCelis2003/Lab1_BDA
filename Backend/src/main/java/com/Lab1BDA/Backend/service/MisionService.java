package com.Lab1BDA.Backend.service;

import com.Lab1BDA.Backend.dto.*;
import com.Lab1BDA.Backend.exception.ResourceNotFoundException;
import com.Lab1BDA.Backend.model.Mision;
import com.Lab1BDA.Backend.model.Usuario;
import com.Lab1BDA.Backend.repository.MisionRepository;
import com.Lab1BDA.Backend.repository.UserRepository;
import org.locationtech.jts.geom.Geometry; // Importación necesaria
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MisionService {

    @Autowired
    private MisionRepository misionRepository;

    @Autowired
    private UserRepository userRepository; // Necesario para obtener el creador

    // Este WKTReader convierte el String del DTO a un objeto LineString
    private final WKTReader wktReader = new WKTReader();

    public List<Mision> getTodasLasMisiones() {
        return misionRepository.findAll();
    }

    public Mision getMisionPorId(Long id) {
        return misionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con id: " + id));
    }

    public Mision crearMision(MisionRequestDTO dto, String emailCreador) {
        // 1. Buscar al usuario creador por su email
        Usuario creador = userRepository.findByEmail(emailCreador)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado: " + emailCreador));

        Mision mision = new Mision();
        mision.setIdTipoMision(dto.idTipoMision());
        mision.setIdDronAsignado(dto.idDronAsignado());
        mision.setFechaInicioPlanificada(dto.fechaInicioPlanificada());
        mision.setFechaFinPlanificada(dto.fechaFinPlanificada());

        // 2. Establecer campos de negocio
        mision.setIdOperadorCreador(creador.getIdUsuario());
        mision.setEstado(dto.estado() != null ? dto.estado() : "Pendiente"); // Default

        // 3. Convertir WKT (String) a LineString (JTS)
        mision.setRuta(convertWKTToLineString(dto.rutaWKT()));

        // 4. Pasa la Mision (con el LineString) al repositorio
        return misionRepository.save(mision);
    }

    public Mision actualizarMision(Long id, MisionRequestDTO dto) {
        // 1. Verificar que la misión existe
        Mision misionExistente = getMisionPorId(id);

        // 2. Actualizar campos
        misionExistente.setIdTipoMision(dto.idTipoMision());
        misionExistente.setIdDronAsignado(dto.idDronAsignado());
        misionExistente.setFechaInicioPlanificada(dto.fechaInicioPlanificada());
        misionExistente.setFechaFinPlanificada(dto.fechaFinPlanificada());
        misionExistente.setEstado(dto.estado());

        // 3. Convertir WKT (String) a LineString (JTS)
        misionExistente.setRuta(convertWKTToLineString(dto.rutaWKT()));

        // 4. Pasa la Mision (con el LineString) al repositorio
        return misionRepository.update(misionExistente);
    }

    public void eliminarMision(Long id) {
        getMisionPorId(id); // Verifica que existe
        misionRepository.deleteById(id);
    }

    /**
     * Helper para convertir un String WKT a un objeto LineString de JTS.
     */
    private LineString convertWKTToLineString(String wkt) {
        if (wkt == null || wkt.isEmpty()) {
            return null;
        }
        try {
            Geometry geom = wktReader.read(wkt);
            if (geom instanceof LineString) {
                return (LineString) geom;
            }
            throw new IllegalArgumentException("El WKT proporcionado no es un LINESTRING");
        } catch (ParseException e) {
            // Esto debería devolver un 400 Bad Request
            throw new IllegalArgumentException("Formato WKT de la ruta es inválido: " + wkt, e);
        }
    }

    /**
     * Lógica de negocio para asignar una misión a un dron.
     * Llama al procedimiento almacenado en la BD.
     * * @param idMision El ID de la misión.
     * @param idDron El ID del dron.
     */
    // Pero como el SP ya es atómico, solo delegamos la llamada.
    public void asignarMisionADron(Long idMision, Long idDron) {
        // La lógica de validación (si el dron está 'Disponible', etc.)
        // ya está encapsulada dentro del Procedimiento Almacenado,
        // cumpliendo el requisito del enunciado[cite: 60].

        // Simplemente llamamos al repositorio.
        misionRepository.asignarMisionADron(idMision, idDron);
    }

    /**
     * Obtiene el reporte de desempeño por tipo de misión (Requisito #3).
     * @return Lista de DesempenoTipoMisionDTO
     */
    /**
     * Obtiene el reporte de desempeño por tipo de misión (Requisito #3).
     * @return Lista de DesempenoTipoMisionDTO
     */
    public List<DesempenoTipoMisionDTO> getReporteDesempenoTipoMision() {
        return misionRepository.findDesempenoPorTipoMision();
    }

    /**
     * Obtiene el reporte de patrones de consumo de batería (Requisito #4).
     * @return Lista de BateriaConsumoDTO
     */
    public List<BateriaConsumoDTO> getReporteConsumoBateria() {
        return misionRepository.findPatronConsumoBateria();
    }

    /**
     * Obtiene el reporte de desempeño mensual (Requisito #5).
     * @return Lista de DesempenoMensualDTO
     */
    public List<DesempenoMensualDTO> getReporteDesempenoMensual() {
        return misionRepository.findDesempenoMensual();
    }

    /**
     * Obtiene el reporte de resumen de misiones por tipo (Requisito #10).
     * @return Lista de ResumenMisionTipoDTO
     */
    public List<ResumenMisionTipoDTO> getReporteResumenMisiones() {
        return misionRepository.findResumenMisionesCompletadas();
    }

}