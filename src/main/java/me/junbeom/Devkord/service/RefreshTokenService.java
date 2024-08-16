package me.junbeom.Devkord.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.config.jwt.TokenProvider;
import me.junbeom.Devkord.domain.RefreshToken;
import me.junbeom.Devkord.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    @Transactional
    public void delete() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long userId = tokenProvider.getUserId(token);

        refreshTokenRepository.deleteByUserId(userId);
    }
}
