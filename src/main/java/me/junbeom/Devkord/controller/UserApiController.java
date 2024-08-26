package me.junbeom.Devkord.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.junbeom.Devkord.domain.User;
import me.junbeom.Devkord.dto.CurrentUserResponse;
import me.junbeom.Devkord.dto.UserLoginRequest;
import me.junbeom.Devkord.dto.UserSignupRequest;
import me.junbeom.Devkord.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5173/")
public class UserApiController {

    private final UserService userService;


    //유찬에게 배포할때 입력인자
    // @RequestBody UserLoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response
    // 로 바꾸고 반환값은
    // return new ResponseEntity<>("Login successful", headers, HttpStatus.OK);
    // 이렇게 바꿔야함
    @PostMapping("users/login")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = userService.signIn(loginRequest.getEmail(), loginRequest.getPassword(), request, response);
        log.info("request email = {}, password = {}", loginRequest.getEmail(), loginRequest.getPassword());
        log.info("jwtToken accessToken = {}", accessToken);

//        // Response 헤더에 accessToken 추가
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//        return new ResponseEntity<>("Login successful", headers, HttpStatus.OK);

        // 응답 바디에 accessToken 추가
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);
        responseBody.put("message", "Login successful");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @PostMapping("/users/signup")
    public ResponseEntity<String> signup(@RequestParam("email") String email,
                                         @RequestParam("password") String password,
                                         @RequestParam("nickname") String nickname,
                                         @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) {
        UserSignupRequest userSignupRequest = new UserSignupRequest();
        userSignupRequest.setEmail(email);
        userSignupRequest.setPassword(password);
        userSignupRequest.setNickname(nickname);
        userSignupRequest.setProfileImg(profileImg);

        userService.save(userSignupRequest);

        return ResponseEntity.ok("Signup successful. Please log in.");
    }


//    @PostMapping("/users/signup")
//    public ModelAndView signup(@RequestParam("email") String email,
//                               @RequestParam("password") String password,
//                               @RequestParam("nickname") String nickname,
//                               @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) {
//
//        UserSignupRequest userSignupRequest = new UserSignupRequest();
//        userSignupRequest.setEmail(email);
//        userSignupRequest.setPassword(password);
//        userSignupRequest.setNickname(nickname);
//        userSignupRequest.setProfileImg(profileImg);
//
//        userService.save(userSignupRequest);
//
//        return new ModelAndView("redirect:/users/login");
//    }

    @GetMapping("/users/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/users/login";
    }

    @GetMapping("/currentUser")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(Authentication authentication) {
        System.out.println("currentUser Controller connected");
        User user = (User) authentication.getPrincipal();
        CurrentUserResponse currentUserResponse = new CurrentUserResponse(user);
        System.out.println(user);
        System.out.println(currentUserResponse);
        return ResponseEntity.ok(currentUserResponse);
    }
}
