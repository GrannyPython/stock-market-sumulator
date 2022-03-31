package com.example.demo.service;

import com.example.demo.dto.CancelOrderRq;
import com.example.demo.dto.CancelOrderRs;
import com.example.demo.dto.CreateOrderRq;
import com.example.demo.dto.CreateOrderRs;
import com.example.demo.entity.Book;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderType;
import com.example.demo.keeper.BookKeeper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private final BookKeeper bookKeeper;
    private final AtomicInteger orderIdGenerator = new AtomicInteger();

    public CreateOrderRs createOrder(CreateOrderRq rq) {
        int orderId = this.orderIdGenerator.getAndIncrement();
        Order order = new Order(orderId, rq.getCompanySymbol(), rq.getPosition(),
                rq.getPrice(), rq.getAmount(), ZonedDateTime.now(), OrderType.CREATE);
        Book book = bookKeeper.addOrderToBook(rq.getCompanySymbol(), order);

        return new CreateOrderRs(orderId, "OK");
    }

    public CancelOrderRs cancelOrder(CancelOrderRq rq) {
        Map<String, Book> books = bookKeeper.getBooks();
        Book book = books.get(rq.getCompanySymbol());
        book.addOrder(new Order(orderIdGenerator.incrementAndGet(), rq.getCompanySymbol(), rq.getPosition(),
                rq.getPrice(), rq.getAmount(), ZonedDateTime.now(), OrderType.CANCEL));

        return new CancelOrderRs("OK");
    }

}
