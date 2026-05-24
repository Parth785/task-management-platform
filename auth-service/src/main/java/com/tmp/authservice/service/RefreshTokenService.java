package com.tmp.authservice.service;

import com.tmp.authservice.entity.RefreshToken;
import com.tmp.authservice.entity.User;
import com.tmp.authservice.repository.RefreshTokenRepository;
import com.tmp.authservice.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // delete any existing refresh token for this user
        // one refresh token per user at a time
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtService.generateRefreshToken())
                .expiryDate(LocalDateTime.now().plus(
                        jwtService.getRefreshExpirationMs(),
                        ChronoUnit.MILLIS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired, please login again");
        }

        return refreshToken;
    }
}