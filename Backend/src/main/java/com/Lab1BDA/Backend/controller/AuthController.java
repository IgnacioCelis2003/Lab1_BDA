package com.Lab1BDA.Backend.controller;

import com.Lab1BDA.Backend.dto.AuthResponse;
import com.Lab1BDA.Backend.dto.LoginRequest;
import com.Lab1BDA.Backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 1. Inyectamos nuestro nuevo AuthService
    @Autowired
    private AuthService authService;

    // 2. Aquí implementamos tu sugerencia de tipo de respuesta estricto
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 3. El controlador solo delega la lógica al servicio
        AuthResponse authResponse = authService.login(loginRequest);

        // 4. Y devuelve el resultado
        return ResponseEntity.ok(authResponse);
    }
}