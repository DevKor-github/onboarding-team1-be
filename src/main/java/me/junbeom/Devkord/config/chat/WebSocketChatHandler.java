package me.junbeom.Devkord.config.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.junbeom.Devkord.domain.Chat;
import me.junbeom.Devkord.domain.ChatRoom;
import me.junbeom.Devkord.dto.ChatSendRequest;
import me.junbeom.Devkord.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final ChatService chatService;

    // 현재 연결된 세션들
    private ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // chatRoomId(String): {session1, session2} 매핑 자료형
    private final Map<String, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} Connected!!", session.getId());
        System.out.println("WEB SOCKET CONNECT"+session.getId());
        sessions.put(session.getId(), session);
        //여기에 과거 채팅내역 가져오는 코드 추가해야할 듯

        //과거채팅내역 db에서 가져오고 클라이언트로 전송
        String chatRoomId = getChatRoomIdFromSession(session);
        System.out.println("ChatRoomId : "+chatRoomId);
        List<Chat> chats = chatService.getAllChats(chatRoomId);
        System.out.println("CHATS : "+chats);
        if (!chats.isEmpty()) {
            for (Chat chat : chats) {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(chat)));
            }
        }
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
        chatRoomSessionMap.putIfAbsent(chatRoomId, ConcurrentHashMap.newKeySet());
        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);


        if (chatSendRequest.getMessageType().equals(ChatSendRequest.MessageType.ENTER)) {
            chatRoomSession.add(session);
            System.out.println("CHATROOMSESSION:" + chatRoomSession);
        }
        sendMessageToChatRoom(chatSendRequest, chatRoomSession);

        // 전송된 메시지를 채팅방에 저장하는 로직 추가
        saveChatToChatRoom(chatSendRequest);
    }

    // 소켓 종료 확인
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} Disconnected!!", session.getId());
        System.out.println("Connection closed: " + session.getId() + " Status: " + status);

        // 채팅방에서 해당 세션 제거
        chatRoomSessionMap.forEach((chatRoomId, sessions) -> sessions.remove(session));

        // 전체 세션 맵에서 세션 제거
        sessions.remove(session.getId());
    }

//-----------------------------기타 메서드들-------------------------
    private void saveChatToChatRoom(ChatSendRequest chatSendRequest) {
        // 0. ENTER 메세지는 저장하지 않음.
        if (chatSendRequest.getMessageType().equals(ChatSendRequest.MessageType.ENTER)) {
            return;
        }

        // 1. ChatSendRequest에서 Chat 엔티티 생성
        Chat chat = new Chat(
                chatSendRequest.getSenderId(),
                chatSendRequest.getMessage(),
                LocalDateTime.now()
        );

        // 2. 해당 ChatRoom을 DB에서 조회
        ChatRoom chatRoom = chatService.findChatRoomById(chatSendRequest.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found: " + chatSendRequest.getChatRoomId()));

        // 3. ChatRoom에 새 메시지 추가
        chatRoom.addChat(chat);
        System.out.println("CHATROOOOM:"+chatRoom);

        // 4. 변경된 ChatRoom을 저장 (채팅 내역을 업데이트)
        chatService.updateChatRoom(chatRoom);
    }

    private String getChatRoomIdFromSession(WebSocketSession session) {
        // URL 쿼리 파라미터에서 chatRoomId 추출
        URI uri = session.getUri();
        String query = uri.getQuery();
        if (query != null) {
            Map<String, String> queryParams = Arrays.stream(query.split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
            return queryParams.get("chatRoomId");
        }
        return null;
    }

    private void sendMessageToChatRoom(ChatSendRequest chatMessageDto, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream()
                .filter(WebSocketSession::isOpen)  // 세션이 열려 있는지 확인
                .forEach(sess -> sendMessage(sess, chatMessageDto));//2
    }


    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}