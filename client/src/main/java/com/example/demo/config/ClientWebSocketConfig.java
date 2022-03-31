package com.example.demo.config;

import com.example.demo.handler.ClientWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class ClientWebSocketConfig {
    @Bean
    public WebSocketConnectionManager webSocketConnectionManager(ObjectMapper mapper) {
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                webSocketClient(),
                webSocketHandler(mapper),
                "ws://localhost:8080/websocket"
        );
        manager.setAutoStartup(true);
        return manager;
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketHandler webSocketHandler(ObjectMapper mapper) {
        return new ClientWebSocketHandler(mapper);
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }
}
