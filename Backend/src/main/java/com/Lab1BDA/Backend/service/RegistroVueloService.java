package com.Lab1BDA.Backend.service;

import com.Lab1BDA.Backend.dto.RegistroVueloRequestDTO;
import com.Lab1BDA.Backend.dto.UbicacionDTO;
import com.Lab1BDA.Backend.model.RegistroVuelo;
import com.Lab1BDA.Backend.repository.RegistroVueloRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistroVueloService {

    @Autowired
    private RegistroVueloRepository registroVueloRepository;

    private final WKTReader wktReader = new WKTReader(); // Para leer el WKT del DTO

    /**
     * Registra un nuevo punto de telemetría.
     * @param dto El DTO con los datos de telemetría.
     * @return El registro de vuelo guardado.
     */
    public RegistroVuelo registrarTelemetria(RegistroVueloRequestDTO dto) {
        RegistroVuelo registro = new RegistroVuelo();
        registro.setIdMision(dto.idMision());
        registro.setAltitudMsnm(dto.altitudMsnm());
        registro.setVelocidadKmh(dto.velocidadKmh());
        registro.setNivelBateriaPorcentaje(dto.nivelBateriaPorcentaje());

        // Si el DTO no trae timestamp, usamos el actual
        registro.setTimestamp(dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now());

        // Convertir el String WKT (ej: "POINT(1 2)") en un objeto Point
        try {
            Geometry geom = wktReader.read(dto.coordenadasWKT());
            if (geom instanceof Point) {
                registro.setCoordenadas((Point) geom);
            } else {
                throw new IllegalArgumentException("El WKT proporcionado no es un POINT");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato WKT de coordenadas inválido", e);
        }

        return registroVueloRepository.save(registro);
    }

    /**
     * Obtiene todos los datos de telemetría para una misión.
     * @param idMision El ID de la misión.
     * @return Lista de registros de telemetría.
     */
    public List<RegistroVuelo> getTelemetriaPorMision(Long idMision) {
        return registroVueloRepository.findByMisionId(idMision);
    }


    /**
     * Pasa un registro de vuelo a UbicacionDTO, para mostrarlo en el mapa.
     * @param registro
     * @return
     */
    public UbicacionDTO registroToUbicacion(RegistroVuelo registro) {
        return new UbicacionDTO(
                registro.getIdMision(),
                registro.getTimestamp(),
                registro.getCoordenadas().getX(),
                registro.getCoordenadas().getY(),
                registro.getNivelBateriaPorcentaje()
        );
    }

    /**
     * Obtiene las ubicaciones de todos los drones activos en un determinado momento.
     * @return
     */
    public List<UbicacionDTO> getMonitoreo(){
        List<RegistroVuelo> registros = registroVueloRepository.findByTimestamp(LocalDateTime.now());
        List<UbicacionDTO> ubicaciones = new ArrayList<>();
        for (RegistroVuelo registro : registros) {
            ubicaciones.add(registroToUbicacion(registro));
        }
        return ubicaciones;
    }
}