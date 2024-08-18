package me.junbeom.Devkord.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatSendRequest {
    // 메시지  타입 : 입장, 채팅
    public enum MessageType{
        ENTER, TALK
    }

    private MessageType messageType; // 메시지 타입
    private String chatRoomId; // 방 번호
    private Long senderId; // 채팅을 보낸 사람
    private String message; // 메시지
}
