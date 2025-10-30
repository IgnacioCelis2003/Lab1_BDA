package com.Lab1BDA.Backend.repository;


import com.Lab1BDA.Backend.model.Usuario;
import com.Lab1BDA.Backend.repository.mappers.UsuarioRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Busca un usuario por su email en la base de datos.
     * @param email El email del usuario a buscar.
     * @return Un Optional<Usuario> que contiene al usuario si se encuentra, o vacío si no.
     */
    public Optional<Usuario> findByEmail(String email) {
        // El SQL ahora selecciona columnas específicas, lo cual es una mejor práctica
        String sql = "SELECT id_usuario, nombre, email, contrasena_hash, rol FROM usuarios WHERE email = ?";

        // Usamos .query() que devuelve una lista. Esto evita la excepción
        // 'EmptyResultDataAccessException' si el usuario no se encuentra.
        List<Usuario> usuarios = jdbcTemplate.query(sql, new UsuarioRowMapper(), email);

        // Si la lista está vacía, retornamos un Optional vacío.
        // Si no, retornamos el primer (y único) usuario encontrado.
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }


}