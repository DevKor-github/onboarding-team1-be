package me.junbeom.Devkord.service;

import lombok.RequiredArgsConstructor;
import me.junbeom.Devkord.domain.Chat;
import me.junbeom.Devkord.domain.ChatRoom;
import me.junbeom.Devkord.domain.User;
import me.junbeom.Devkord.repository.ChatRoomRepository;
import me.junbeom.Devkord.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;

    public List<Chat> getAllChats(String chatRoomId) {
        System.out.println("getAllChats START");
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected ChatRoom :("));
        System.out.println("CHATROOM::: "+chatRoom);
        return chatRoom.getChats();
    }

    //채팅룸 조회(없을 시 생성)
    public void saveChatRoom(String chatRoomId, Long user1Id, Long user2Id) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseGet(() -> createNewChatRoom(chatRoomId, user1Id, user2Id));
    }

    //채팅룸생성
    private ChatRoom createNewChatRoom(String chatRoomId, Long user1Id, Long user2Id) {
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .userId1(user1Id)
                .userId2(user2Id)
                .build();

        // ChatRoom을 DB에 저장
        chatRoomRepository.save(chatRoom);

        return chatRoom;
    }

    // 새로운 ChatRoom 조회 메서드
    public Optional<ChatRoom> findChatRoomById(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId);
    }

    // 변경된 ChatRoom을 저장하는 메서드
    public void updateChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }
}
