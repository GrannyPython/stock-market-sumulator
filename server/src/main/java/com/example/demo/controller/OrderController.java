package com.example.demo.controller;

import com.example.demo.dto.CancelOrderRq;
import com.example.demo.dto.CancelOrderRs;
import com.example.demo.dto.CreateOrderRq;
import com.example.demo.dto.CreateOrderRs;
import com.example.demo.exception.ValidationException;
import com.example.demo.service.OrderService;
import com.example.demo.util.JsonUtils;
import com.example.demo.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Validator validator;
    private final JsonUtils jsonUtils;

    @PostMapping("order")
    public  CreateOrderRs createOrder(@RequestBody CreateOrderRq rq) {
        log.info("RQ={}", rq);

        CreateOrderRs rs = null;
        try {
            validator.validate(rq);
            rs = orderService.createOrder(rq);
        } catch (ValidationException ex) {
            log.error("Validation failed for rq={}, err={}", jsonUtils.toJson(rq), jsonUtils.toJson(ex.getErrors()));
            rs = new CreateOrderRs("validation failed");
        } catch (Throwable ex) {
            log.error("Exception for rq={}, ex=", jsonUtils.toJson(rq), ex);
            rs = new CreateOrderRs("exception");
        } finally {
            log.info("RQ={}, RS={}", rq, rs);
        }

        return rs;
    }

    @PutMapping("order")
    public CancelOrderRs cancelOrder(CancelOrderRq rq) {
        log.info("RQ={}", rq);

        CancelOrderRs rs = null;
        try {
            validator.validate(rq);
            rs = orderService.cancelOrder(rq);
        } catch (ValidationException ex) {
            log.error("Validation failed for rq={}, err={}", jsonUtils.toJson(rq), jsonUtils.toJson(ex.getErrors()));
            rs = new CancelOrderRs("validation failed");
        } catch (Throwable ex) {
            log.error("Exception for rq={}, ex=", jsonUtils.toJson(rq), ex);
            rs = new CancelOrderRs("exception");
        } finally {
            log.info("RQ={}, RS={}", rq, rs);
        }

        return rs;
    }

}
