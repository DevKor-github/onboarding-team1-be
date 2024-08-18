package me.junbeom.Devkord.config.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.junbeom.Devkord.dto.ChatSendRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;

    // 현재 연결된 세션들
    private final Set<WebSocketSession> sessions = new HashSet<>();

    // chatRoomId(String): {session1, session2} 매핑 자료형
    private final Map<String,Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} Connected!!", session.getId());
        sessions.add(session);

        //여기에 과거 채팅내역 가져오는 코드 추가해야할 듯
    }

    // 소켓 통신 시 메세지의 전송을 다루는 부분
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        // 페이로드 -> chatMessageDto로 변환
        ChatSendRequest chatSendRequest = mapper.readValue(payload, ChatSendRequest.class);
        log.info("session {}", chatSendRequest.toString());

        //chatSendRequest에서 ChatRoomId를 가져와서 채팅룸(chatRoomSession)을 생성.(이미 존재하면 조회해서 chatRoomSession에 저장)
        String chatRoomId = chatSendRequest.getChatRoomId();
        // 메모리 상에 채팅방에 대한 세션 없으면 만들어줌
        if(!chatRoomSessionMap.containsKey(chatRoomId)){
            chatRoomSessionMap.put(chatRoomId,new HashSet<>());
        }
        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);

        // message 에 담긴 타입을 확인한다.
        // 이때 message 에서 getType 으로 가져온 내용이
        // ChatDTO 의 열거형인 MessageType 안에 있는 ENTER 과 동일한 값이라면
        if (chatSendRequest.getMessageType().equals(ChatSendRequest.MessageType.ENTER)) {
            // sessions 에 넘어온 session 을 담고,
            chatRoomSession.add(session);
        }
        if (chatRoomSession.size()>=3) {
            removeClosedSession(chatRoomSession);
        }
        sendMessageToChatRoom(chatSendRequest, chatRoomSession);

        //여기에 전송한 메시지 채팅룸 메시지역사에 추가하는 코드 추가해야할 듯

    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} Disconnected!!", session.getId());
        sessions.remove(session);
    }

    // ====== 채팅 관련 메소드 ======
    private void removeClosedSession(Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.removeIf(sess -> !sessions.contains(sess));
    }

    private void sendMessageToChatRoom(ChatSendRequest chatMessageDto, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatMessageDto));//2
    }


    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}