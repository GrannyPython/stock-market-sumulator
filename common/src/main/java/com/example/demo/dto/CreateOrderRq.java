package com.example.demo.dto;

import com.example.demo.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderRq {
    private String companySymbol;
    private Position position;
    private Integer amount;
    private Integer price;
}