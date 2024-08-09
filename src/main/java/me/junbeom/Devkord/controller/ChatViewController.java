package me.junbeom.Devkord.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ChatViewController {
    @GetMapping("/chat/list")
    public String getChatlist() {
        return "chatlist";
    }
}
