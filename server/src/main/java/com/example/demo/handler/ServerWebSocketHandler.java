package com.example.demo.handler;

import com.example.demo.entity.Trade;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class ServerWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    @Autowired
    private ObjectMapper mapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Server connection opened");
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Server connection closed: {}", status);
        sessions.remove(session);
    }

    public void sendTrade(Trade trade) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mapper.writeValueAsString(trade)));
                } catch (IOException ex) {
                    log.error("Error=", ex);
                }
            }
        }
    }

    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Server received: {}", message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("Server transport error: {}", exception.getMessage());
    }

}
