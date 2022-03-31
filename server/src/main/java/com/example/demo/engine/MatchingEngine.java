package com.example.demo.engine;

import com.example.demo.entity.Book;
import com.example.demo.entity.Trade;
import com.example.demo.keeper.BookKeeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchingEngine {
    private final BookKeeper keeper;
    private final TradeLedger ledger;

    @Scheduled(fixedDelay = 1000L)
    private void matchBooks() {
        for (Book book : keeper.getBooks().values()) {
            balance(book);
        }
    }

    public void balance(Book book) {
        synchronized (book) {
            if (book.isEmpty()) {
                keeper.deleteBook(book);
            }
            List<Trade> trades = book.balance();
            for (Trade trade : trades) {
                ledger.save(trade);
            }
        }
    }
}
