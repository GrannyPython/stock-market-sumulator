package com.example.demo.keeper;

import com.example.demo.entity.Book;
import com.example.demo.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BookKeeper {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    @Autowired
    private AtomicInteger tradeIdGenerator;

    public Map<String, Book> getBooks() {
        return books;
    }

    public synchronized Book addOrderToBook(String companySymbol, Order order) {
        Book book = books.computeIfAbsent(companySymbol, (stub) -> new Book(companySymbol, tradeIdGenerator));
        book.addOrder(order);
        return book;
    }

    public synchronized Book getBook(String companySymbol) {
        return books.get(companySymbol);
    }

    public synchronized void deleteBook(Book book) {
        books.remove(book.getSymbols());
    }
}
