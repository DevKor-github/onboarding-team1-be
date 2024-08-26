package me.junbeom.Devkord.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.dto.CreateAccessTokenRequest;
import me.junbeom.Devkord.dto.CreateAccessTokenResponse;
import me.junbeom.Devkord.service.RefreshTokenService;
import me.junbeom.Devkord.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5173/")
public class TokenApiController {
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    //엑세스토큰 재발급
    //클라이언트에서 토큰 유효성판단했는데 만료(서버로 요청보냈는데 error반환(by 토큰필터))라는 답을 받으면 그 때 api/token으로 post요청 보냄 with 리프레시토큰
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
            (@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

//        //헤더로 토큰 전달
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
//                .build();

        //바디로 토큰 전달하는 코드(주석처리)
        return ResponseEntity.status(HttpStatus.CREATED)
             .body(new CreateAccessTokenResponse(newAccessToken));
    }

    @DeleteMapping("/api/refresh-token")
    public ResponseEntity deleteRefreshToken() {
        refreshTokenService.delete();

        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/api/check-token")
    public ResponseEntity checkAceessToken() {

        //여기에 헤더에 있는 토큰의 유저정보 조회해서 현재사용자와 같은지 확인하는 로직 추가해야함//

        return ResponseEntity.ok()
                .build();
    }
}
