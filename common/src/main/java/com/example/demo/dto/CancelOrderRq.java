package com.example.demo.dto;

import com.example.demo.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CancelOrderRq {
    private Integer id;
    private String companySymbol;
    private Position position;
    private int amount;
    private Integer price;
}
