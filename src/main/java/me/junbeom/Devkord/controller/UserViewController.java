package me.junbeom.Devkord.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "http://localhost:5173/")
public class UserViewController {
    @GetMapping("/users/login")
    public String login() {
        return "oauthLogin";
    }

    @GetMapping("/users/signup")
    public String signup() {
        return "signup";
    }
}
