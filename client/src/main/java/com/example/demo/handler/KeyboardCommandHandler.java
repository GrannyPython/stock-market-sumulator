package com.example.demo.handler;

import com.example.demo.dto.CreateOrderRq;
import com.example.demo.dto.CreateOrderRs;
import com.example.demo.entity.Position;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeyboardCommandHandler {
    public static final String UNKNOWN_COMMAND = "Unknown command, call \"help\" or \"man\" for documentation";
    public static final int PARAMS_NUM = 5;
    @Autowired
    private final RestTemplate template;
    @Autowired
    private final ObjectMapper mapper;

    public void process(String input) {
        if (StringUtils.isEmpty(input)) {
            log.warn("Empty input is not allowed, try \"man\" or \"help\"");
            return;
        }
        input = input.trim();
        if ("help".equals(input) || "man".equals(input)) {
            log.info("Example of valid command:");
            log.info("add GOOG B 100 50");
            log.info("add GOOG S 100 50");
            return;
        }

        String[] parts = input.split(" ");
        if (parts.length != PARAMS_NUM) {
            log.warn(UNKNOWN_COMMAND);
            return;
        }
        String symbol = parts[1];
        String position = parts[2];
        String amount = parts[3];
        String price = parts[4];
        if (isCorrect(parts)) {
            try {
                CreateOrderRq rq = new CreateOrderRq(symbol, Position.fromString(position.toUpperCase()),
                        Integer.parseInt(price), Integer.parseInt(amount));
                HttpEntity<Object> request = new HttpEntity<>(mapper.writeValueAsString(rq), getHttpHeaders());
                CreateOrderRs rs = template.postForEntity("http://localhost:8080/order",
                        request, CreateOrderRs.class).getBody();
                log.info("Order with ID {} added: {} {} amount={} @ price={}", rs.getId(), rq.getCompanySymbol(),
                        rq.getPosition().name(), rq.getAmount(), rq.getPrice());
            } catch (JsonProcessingException e) {
                log.error("serialization error=", e);
            }
        } else {
            log.warn(UNKNOWN_COMMAND);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private boolean isCorrect(String[] parts) {
        if (!("add".equalsIgnoreCase(parts[0]))) {
            return false;
        }
        if (!("B".equalsIgnoreCase(parts[2]) || "S".equalsIgnoreCase(parts[2]))) {
            return false;
        }
        try {
            int amount = Integer.parseInt(parts[3]);
        } catch (Exception ex) {
            log.warn("Wrong amount, try again");
            return false;
        }

        try {
            int price = Integer.parseInt(parts[4]);
        } catch (Exception ex) {
            log.warn("Wrong price, try again");
            return false;
        }
        return true;
    }
}
