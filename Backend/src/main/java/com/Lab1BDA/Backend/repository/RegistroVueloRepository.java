package com.Lab1BDA.Backend.repository;

import com.Lab1BDA.Backend.model.RegistroVuelo;
import com.Lab1BDA.Backend.repository.mappers.RegistroVueloRowMapper;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RegistroVueloRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Consulta base para LEER registros (usando ST_AsWKT como en MisionRowMapper)
    private final String BASE_SELECT = "SELECT id_registro_vuelo, id_mision, \"timestamp\", " +
            "altitud_msnm, velocidad_kmh, nivel_bateria_porcentaje, " +
            "ST_AsText(coordenadas) AS coordenadas_wkt " +
            "FROM registro_vuelo";

    /**
     * Guarda un nuevo registro de telemetría en la base de datos.
     * @param registro El objeto RegistroVuelo a guardar.
     * @return El objeto RegistroVuelo guardado (con su ID).
     */
    public RegistroVuelo save(RegistroVuelo registro) {
        // Consulta para ESCRIBIR (usando ST_GeogFromWKB como en MisionRepository)
        String sql = "INSERT INTO registro_vuelo (id_mision, \"timestamp\", coordenadas, " +
                "altitud_msnm, velocidad_kmh, nivel_bateria_porcentaje) " +
                "VALUES (?, ?, ST_GeogFromWKB(?), ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        WKBWriter wkbWriter = new WKBWriter(); // Para convertir Point a byte[]

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, registro.getIdMision());
            ps.setObject(2, registro.getTimestamp()); // setObject maneja LocalDateTime

            // Convertimos el Point de JTS a WKB (byte[])
            if (registro.getCoordenadas() != null) {
                ps.setBytes(3, wkbWriter.write(registro.getCoordenadas()));
            } else {
                ps.setNull(3, Types.BINARY);
            }

            ps.setObject(4, registro.getAltitudMsnm()); // setObject maneja Double null
            ps.setObject(5, registro.getVelocidadKmh());
            ps.setDouble(6, registro.getNivelBateriaPorcentaje());
            return ps;
        }, keyHolder);

        // Obtenemos el ID generado
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id_registro_vuelo")) {
            registro.setIdRegistroVuelo(((Number) keyHolder.getKeys().get("id_registro_vuelo")).longValue());
        }

        return registro;
    }

    /**
     * Busca todos los registros de telemetría para una misión específica.
     * @param idMision El ID de la misión.
     * @return Una lista de registros de vuelo, ordenados por timestamp.
     */
    public List<RegistroVuelo> findByMisionId(Long idMision) {
        String sql = BASE_SELECT + " WHERE id_mision = ? ORDER BY \"timestamp\" ASC";
        return jdbcTemplate.query(sql, new RegistroVueloRowMapper(), idMision);
    }

    /**
     * Busca todos los registros de telemetría con cierto timestamp.
     * @param timestamp
     * @return
     */
    public List<RegistroVuelo> findByTimestamp(LocalDateTime timestamp) {
        String sql = BASE_SELECT + " WHERE timestamp = ?";
        return jdbcTemplate.query(sql, new RegistroVueloRowMapper(), timestamp);
    }
}