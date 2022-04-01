package com.example.demo.listener;

import com.example.demo.handler.KeyboardCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
@Slf4j
class EventListener {
    private final KeyboardCommandHandler handler;

    @org.springframework.context.event.EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
        Scanner scanner = new Scanner(System.in);
        log.info("Client ready to work, type something");
        while (true) {
            String input = scanner.nextLine();
            try {
                handler.process(input);
            } catch (RuntimeException ex) {
                log.error("Ex=", ex);
            }
        }
    }
}