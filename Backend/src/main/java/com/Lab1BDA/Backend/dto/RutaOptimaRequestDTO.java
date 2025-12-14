package com.Lab1BDA.Backend.dto;

import java.util.List;

/**
 * DTO para manejar las peticiones de asignación de misiones a los drones com una ruta óptima para cada dron
 */
public record RutaOptimaRequestDTO(
        List<Long> idMisiones,      // ID de las misiones que se quieren completar
        List<Long> idDrones         // ID de los drones disponibles para el trabajo
){}
