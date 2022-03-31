package com.example.demo.entity;

public enum Company {
    GOOGLE("google inc", "GOOGL"), APPLE("apple ltd", "APPL");

    Company(String name, String symbols) {
        this.name = name;
        this.symbols = symbols;
    }

    public final String name;
    public final String symbols;
}
