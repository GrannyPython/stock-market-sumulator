package com.example.demo.entity;

import lombok.*;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Order implements Comparable<Order> {
    private int orderId;
    private String companySymbols;
    private Position position;
    private int price;
    private int amount;
    private ZonedDateTime orderCreatedDateTime;
    private OrderType type;

    @Override
    public int compareTo(Order o) {
        if (this.equals(o)) {
            return 0;
        }

        if (this.position != o.position) {
            if (this.position == Position.SELL) {
                return -1;
            } else {
                return 1;
            }
        }

        if (this.getPrice() != o.getPrice()) {
            if (this.getPrice() < o.getPrice()) {
                return 1;
            } else {
                return -1;
            }
        }

        long thisDT = this.getOrderCreatedDateTime().toEpochSecond();
        long thatDT = o.getOrderCreatedDateTime().toEpochSecond();
        if (thisDT != thatDT) {
            if (thisDT < thatDT) {
                return 1;
            } else
                return -1;
        }

        return 0;
    }

}