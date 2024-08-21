package me.junbeom.Devkord.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Chat {
    private Long senderId; // 채팅을 보낸 사람
    private String message; // 메시지
    private LocalDateTime timestamp; // 보낸 시간
}