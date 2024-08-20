package me.junbeom.Devkord.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.domain.User;
import me.junbeom.Devkord.dto.AddUserRequest;
import me.junbeom.Devkord.dto.CurrentUserResponse;
import me.junbeom.Devkord.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/users/login";
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
