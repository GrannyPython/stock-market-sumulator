package com.example.demo.entity;


import lombok.Getter;

public enum Position {
    BUY("B"), SELL("S");

    Position(String name) {
        this.shortName = name;
    }

    @Getter
    private final String shortName;

    public static Position fromString(String text) {
        for (Position b : Position.values()) {
            if (b.shortName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
