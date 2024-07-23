package com.example.finalproject.security.controller;

import com.example.finalproject.security.jwt.JwtRequest;
import com.example.finalproject.security.jwt.JwtRequestRefresh;
import com.example.finalproject.security.jwt.JwtResponse;
import com.example.finalproject.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication controller", description = "Controller for authentication of users and getting/refreshing JWTs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "Logging in a user", description = "Provides functionality for logging in a user")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) throws AuthException {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Getting a JWT", description = "Provides functionality for getting an access and refresh JWT")
    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody JwtRequestRefresh request) throws AuthException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Refreshing a JWT", description = "Provides functionality for getting an access and refresh JWT")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody JwtRequestRefresh request) throws AuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}