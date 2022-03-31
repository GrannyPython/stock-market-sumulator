package com.example.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsonUtils {
    private final ObjectMapper mapper;

    public Object toJson(Object obj) {
        return new Object() {
            @Override
            public String toString() {
                try {
                    return mapper.writeValueAsString(obj);
                } catch (JsonProcessingException e) {
                    log.error("Conversion to JSON failed", e);
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }
}
