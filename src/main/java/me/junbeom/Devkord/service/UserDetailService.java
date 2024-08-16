package me.junbeom.Devkord.service;

import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.config.jwt.TokenProvider;
import me.junbeom.Devkord.repository.UserRepository;
import me.junbeom.Devkord.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    //현재 로그인된 유저의 id 가져오는 메서드
    public User getCurrentUser(String token) {
        Long userId = tokenProvider.getUserId(token);
        return findById(userId);
    }
}
