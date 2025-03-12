package cz.syntaxbro.erpsystem.security.websocket;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.utils.ConsoleColors;
import cz.syntaxbro.live.observer.WebServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;


public class ProductWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Handle incoming messages if needed
        ErpSystemApplication.getLogger().info(ConsoleColors.GREEN + "Received text message: {}", message.getPayload());

        var reponse = WebServerResponse
                .builder()
                .type("hearbeat")
                .message("Alive")
                .clientId(null)
                .serverSessionId(session.getId())
                .build();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(reponse)));
    }

    public void broadcast(Object notification) {
        String message;
        try {
            message = objectMapper.writeValueAsString(notification);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    var reponse = WebServerResponse
                            .builder()
                            .type("update")
                            .message(message)
                            .clientId(null)
                            .serverSessionId(session.getId())
                            .build();
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(reponse)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}