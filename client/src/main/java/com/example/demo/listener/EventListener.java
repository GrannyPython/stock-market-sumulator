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
    void contextRefreshedEvent() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Client ready to work, type something");
        while (true) {
            String input = scanner.nextLine();
            try {
                handler.process(input);
            } catch (RuntimeException ex) {
                log.info("Ex=", ex);
            }
        }
    }
}