package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancelOrderRs {
    private String status;

    public CancelOrderRs(String status) {
        this.status = status;
    }
}
