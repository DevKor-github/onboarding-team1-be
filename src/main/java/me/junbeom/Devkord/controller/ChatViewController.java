package me.junbeom.Devkord.controller;

import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.domain.ChatRoom;
import me.junbeom.Devkord.domain.User;
import me.junbeom.Devkord.repository.ChatRoomRepository;
import me.junbeom.Devkord.service.ChatService;
import me.junbeom.Devkord.service.UserDetailService;
import me.junbeom.Devkord.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "http://localhost:5173/")
public class ChatViewController {
    private final UserService userService;
    private final UserDetailService userDetailService;
    private final ChatService chatService;

    @GetMapping("/chat/list")
    public ResponseEntity<Map<String, Object>> getChatlist(@RequestParam("token") String token) {
        // 전체 유저 목록(리스트) 가져오기
        List<User> users = userService.getAllUsers();

        // 현재 로그인된 유저 정보 가져오기
        User currentUser = userDetailService.getCurrentUser(token);

        // 데이터를 Map으로 묶기
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("currentUser", currentUser);

        // ResponseEntity로 반환
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/{id1}/{id2}")
    public String getChatRoomId(@PathVariable Long id1, @PathVariable Long id2, RedirectAttributes redirectAttributes, Authentication authentication) {
        Long user1Id = Math.min(id1, id2);
        Long user2Id = Math.max(id1, id2);

        // 위에서 정렬된 user1Id와 user2Id를 사용하여 채팅방을 찾거나 생성
        String chatRoomId = user1Id + "-" + user2Id;

        System.out.println("현재사용자currentuserINCHAT:"+authentication);

        //채팅룸 db에 저장(db에 이미 있으면 do nothing)
        chatService.saveChatRoom(chatRoomId, user1Id, user2Id);

        redirectAttributes.addAttribute("chatRoomId", chatRoomId);
        redirectAttributes.addAttribute("currentUserId", id1);

        return "redirect:/chat/{chatRoomId}";
    }

    @GetMapping("/chat/{chatRoomId}")
    public String getChatRoom(@PathVariable String chatRoomId, @RequestParam Long currentUserId, Model model) {
        model.addAttribute("chatRoomId", chatRoomId);

        User currentUser = userService.findById(currentUserId);
        model.addAttribute("currentUser", currentUser);

        System.out.println("printed here");

        return "chat";
    }
}
