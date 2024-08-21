package me.junbeom.Devkord.repository;

import me.junbeom.Devkord.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByChatRoomId(String chatRoomId);
}
