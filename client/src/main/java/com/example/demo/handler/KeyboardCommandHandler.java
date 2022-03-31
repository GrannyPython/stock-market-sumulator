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

@Slf4j
@Component
@RequiredArgsConstructor
public class KeyboardCommandHandler {
    public static final String UNKNOWN_COMMAND = "Unknown command, call \"help\" for documentation";
    @Autowired
    private final RestTemplate template;
    @Autowired
    private final ObjectMapper mapper;

    public void process(String input) {
        if (input == null || input.trim().length() == 0) {
            System.out.println("Empty input not allowed, try \"man\" or \"help\"");
            return;
        }
        input = input.trim();
        if (input.equals("help") | input.equals("man")) {
            System.out.println("Example of valid command:");
            System.out.println("add GOOG B 100 50");
            System.out.println("add GOOG S 100 50");
            return;
        }

        String[] parts = input.split(" ");
        if (parts.length != 5) {
            System.out.println(UNKNOWN_COMMAND);
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
            System.out.println(UNKNOWN_COMMAND);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private boolean isCorrect(String[] parts) {
        if (!(parts[0].equalsIgnoreCase("add"))) {
            return false;
        }
        if (!(parts[2].equalsIgnoreCase("B") || parts[2].equalsIgnoreCase("S"))) {
            return false;
        }
        try {
            int amount = Integer.parseInt(parts[3]);
        } catch (Exception ex) {
            System.out.println("Wrong amount, try again");
            return false;
        }

        try {
            int price = Integer.parseInt(parts[4]);
        } catch (Exception ex) {
            System.out.println("Wrong price, try again");
            return false;
        }
        return true;
    }
}
