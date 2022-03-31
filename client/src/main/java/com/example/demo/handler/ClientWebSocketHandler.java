package com.example.demo.handler;

import com.example.demo.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Client connection opened");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Client connection closed: {}", status);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Trade trade = mapper.readValue(message.getPayload(), Trade.class);
            log.info("New execution with ID {}: {} amount={} @ price={} (orders {} and {})", trade.getTradeId(),
                    trade.getCompanySymbols(), trade.getAmount(), trade.getPrice(), trade.getBuyOrderId(),
                    trade.getSellOrderId());
        } catch (JsonProcessingException e) {
            log.error("Json deserialization failed", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("Client transport error: {}", exception.getMessage());
    }
}
