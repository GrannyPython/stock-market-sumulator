package com.example.demo.entity;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class Trade {
    int sellOrderId;
    int buyOrderId;
    int tradeId;
    String companySymbols;
    int amount;
    int price;
}
