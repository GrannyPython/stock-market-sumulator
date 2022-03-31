package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class AppConfig {
    @Bean
    AtomicInteger tradeIdGenerator() {
        return new AtomicInteger();
    }
}
