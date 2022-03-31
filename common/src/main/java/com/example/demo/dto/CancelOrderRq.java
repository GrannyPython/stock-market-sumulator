package com.example.demo.dto;

import com.example.demo.entity.Position;
import lombok.Value;

@Value
public class CancelOrderRq {
    Integer id;
    String companySymbol;
    Position position;
    int amount;
    Integer price;
}
