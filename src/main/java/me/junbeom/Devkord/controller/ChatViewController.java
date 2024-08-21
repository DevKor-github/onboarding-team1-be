package me.junbeom.Devkord.controller;

import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.domain.ChatRoom;
import me.junbeom.Devkord.domain.User;
import me.junbeom.Devkord.repository.ChatRoomRepository;
import me.junbeom.Devkord.service.ChatService;
import me.junbeom.Devkord.service.UserDetailService;
import me.junbeom.Devkord.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatViewController {
    private final UserService userService;
    private final UserDetailService userDetailService;
    private final ChatService chatService;

    @GetMapping("/chat/list")
    public String getChatlist(Model model, @RequestParam("token") String token) {
        List<User> users = userService.getAllUsers(); // 전체 유저 목록(리스트) 가져오기
        model.addAttribute("users", users);

        User currentUser = userDetailService.getCurrentUser(token); // 현재 로그인된 유저의 ID 가져오기
        System.out.println(currentUser);
        model.addAttribute("currentUser", currentUser);


        return "chatlist";
    }

    @GetMapping("/chat/{id1}/{id2}")
    public String getChatRoomId(@PathVariable Long id1, @PathVariable Long id2, RedirectAttributes redirectAttributes) {
        Long user1Id = Math.min(id1, id2);
        Long user2Id = Math.max(id1, id2);

        // 위에서 정렬된 user1Id와 user2Id를 사용하여 채팅방을 찾거나 생성
        String chatRoomId = user1Id + "-" + user2Id;

        //채팅룸 db에 저장(db에 이미 있으면 do nothing)
        chatService.saveChatRoom(chatRoomId, user1Id, user2Id);

        redirectAttributes.addAttribute("chatRoomId", chatRoomId);

        return "redirect:/chat/{chatRoomId}";
    }

    @GetMapping("/chat/{chatRoomId}")
    public String getChatRoom(@PathVariable String chatRoomId, Model model) {
        model.addAttribute("chatRoomId", chatRoomId);

        return "chat";
    }
}
