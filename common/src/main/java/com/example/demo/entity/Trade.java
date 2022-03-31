package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    private int sellOrderId;
    private int buyOrderId;
    private int tradeId;
    private String companySymbols;
    private int amount;
    private int price;
}
