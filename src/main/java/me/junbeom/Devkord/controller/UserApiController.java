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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;


    //유찬에게 배포할때 입력인자
    // @RequestBody UserLoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response
    // 로 바꾸고 반환값은
    // return new ResponseEntity<>("Login successful", headers, HttpStatus.OK);
    // 이렇게 바꿔야함
    @PostMapping("users/login")
    public ModelAndView signIn(@RequestParam String email, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = userService.signIn(email, password, request, response);
            log.info("request email = {}, password = {}", email, password);
            log.info("jwtToken accessToken = {}", accessToken);
            return new ModelAndView("redirect:/chat/list?token=" + accessToken);
        } catch (Exception e) {
            log.error("Error during sign-in", e);
            return new ModelAndView("error"); // 적절한 오류 페이지로 리디렉션하거나 오류 메시지를 반환
        }
    }

    @PostMapping("/users/signup")
    public ModelAndView signup(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("nickname") String nickname,
                               @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) {

        UserSignupRequest userSignupRequest = new UserSignupRequest();
        userSignupRequest.setEmail(email);
        userSignupRequest.setPassword(password);
        userSignupRequest.setNickname(nickname);
        userSignupRequest.setProfileImg(profileImg);

        userService.save(userSignupRequest);

        return new ModelAndView("redirect:/users/login");
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
