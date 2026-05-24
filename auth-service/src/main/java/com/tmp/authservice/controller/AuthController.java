package com.tmp.authservice.controller;

import com.tmp.authservice.dto.request.LoginRequest;
import com.tmp.authservice.dto.request.RefreshTokenRequest;
import com.tmp.authservice.dto.request.RegisterRequest;
import com.tmp.authservice.dto.response.AuthResponse;
import com.tmp.authservice.dto.response.UserResponse;
import com.tmp.authservice.entity.RefreshToken;
import com.tmp.authservice.service.RefreshTokenService;
import com.tmp.authservice.service.UserService;
import com.tmp.authservice.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(request, authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(
                request.getRefreshToken());

        String newAccessToken = jwtService.generateToken(
                refreshToken.getUser().getId(),
                refreshToken.getUser().getRole().name());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
                refreshToken.getUser());

        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                "Bearer",
                refreshToken.getUser().getRole().name()
        ));
    }
}