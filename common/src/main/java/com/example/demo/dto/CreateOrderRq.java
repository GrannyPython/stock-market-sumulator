package com.example.demo.dto;

import com.example.demo.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRq {
    private String companySymbol;
    private Position position;
    private Integer price;
    private Integer amount;
}