package com.example.demo.engine;

import com.example.demo.entity.Trade;
import com.example.demo.handler.ServerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeLedger {
    private final ServerWebSocketHandler webSocketHandler;
    private final List<Trade> trades = new CopyOnWriteArrayList<>();

    public void save(Trade trade) {
        log.info("new Trade created={}", trade);
        this.trades.add(trade);
        webSocketHandler.sendTrade(trade);
    }
}
