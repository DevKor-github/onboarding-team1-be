package me.junbeom.Devkord.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
