package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRs {
    private Integer id;
    private String status;

    public CreateOrderRs(String status) {
        this.status = status;
    }
}
