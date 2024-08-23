package me.junbeom.Devkord.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.junbeom.Devkord.domain.User;
import me.junbeom.Devkord.dto.CurrentUserResponse;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("users/login")
    public ResponseEntity<String> signIn(@RequestParam String email, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = userService.signIn(email, password, request, response);
        log.info("request email = {}, password = {}", email, password);
        log.info("jwtToken accessToken = {}", accessToken);

        // Response 헤더에 accessToken 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        return new ResponseEntity<>("Login successful", headers, HttpStatus.OK);
    }

    @PostMapping("/users/signup")
    public ModelAndView signup(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("nickname") String nickname,
                               @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) {

        byte[] profileImgBytes = null;
        if (profileImg != null && !profileImg.isEmpty()) {
            try {
                profileImgBytes = profileImg.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return new ModelAndView("Failed to process profile image.");
            }
        }

        UserSignupRequest userSignupRequest = new UserSignupRequest();
        userSignupRequest.setEmail(email);
        userSignupRequest.setPassword(password);
        userSignupRequest.setNickname(nickname);
        userSignupRequest.setProfileImg(profileImgBytes);

        userService.save(userSignupRequest);

        return new ModelAndView("redirect:/users/login");
    }

    @GetMapping("/users/profileImage/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        User user = userService.findById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        byte[] profileImg = user.getProfileImg();

        if (profileImg == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(profileImg, headers, HttpStatus.OK);
    }

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
