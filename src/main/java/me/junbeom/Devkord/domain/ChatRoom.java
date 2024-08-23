package me.junbeom.Devkord.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "chatRoomId", nullable = false, unique = true)
    private String chatRoomId;

    @Column(name = "userId1", nullable = false)
    private Long userId1;

    @Column(name = "userId2", nullable = false)
    private Long userId2;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_messages", joinColumns = @JoinColumn(name = "chatRoomId"))
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public ChatRoom(String chatRoomId, Long userId1, Long userId2) {
        this.chatRoomId = chatRoomId;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    // chats에 채팅 내역을 추가하는 메서드
    public void addChat(Chat chat) {
        this.chats.add(chat);
    }
}
