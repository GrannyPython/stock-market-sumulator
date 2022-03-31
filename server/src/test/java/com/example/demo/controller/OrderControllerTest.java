package com.example.demo.controller;

import com.example.demo.dto.CancelOrderRq;
import com.example.demo.dto.CreateOrderRq;
import com.example.demo.engine.MatchingEngine;
import com.example.demo.entity.Book;
import com.example.demo.entity.Position;
import com.example.demo.keeper.BookKeeper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderControllerTest {
    private static final String GOOGL = "GOOGL";
    private static final String AAPL = "APPLE";
    @Autowired
    OrderController controller;
    @Autowired
    MatchingEngine engine;
    @Autowired
    BookKeeper keeper;

    @Test
    void createBuyOrder() {
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.BUY, 0, 1);
        controller.createOrder(createOrderRq1);
        Book book = keeper.getBook(GOOGL);
        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(1, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());
    }

    @Test
    void createSellOrder() {
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.SELL, 0, 1);
        controller.createOrder(createOrderRq1);
        Book book = keeper.getBook(GOOGL);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());
    }

    @Test
    void createBuyOrderThenMatchThenSellOrderAndMatch() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.SELL, 1, 1);
        controller.createOrder(createOrderRq0);
        Book book = keeper.getBook(GOOGL);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.BUY, 1, 1);
        controller.createOrder(createOrderRq1);

        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());
    }

    @Test
    void createBigBuyOrderAndSmallSellOrder() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.SELL, 1, 1);
        controller.createOrder(createOrderRq0);
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.BUY, 1, 10);
        controller.createOrder(createOrderRq1);

        Book book = keeper.getBook(GOOGL);
        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);

        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(1, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

    }

    @Test
    void createBigSellOrderAndSmallBuyOrder() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.BUY, 1, 1);
        controller.createOrder(createOrderRq0);
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.SELL, 1, 10);
        controller.createOrder(createOrderRq1);

        Book book = keeper.getBook(GOOGL);
        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);

        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());

    }

    @Test
    void createBuyOrderFor2Companies() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(AAPL, Position.BUY, 1, 1);
        controller.createOrder(createOrderRq0);
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.BUY, 1, 10);
        controller.createOrder(createOrderRq1);

        Book bookG = keeper.getBook(GOOGL);
        Book bookA = keeper.getBook(AAPL);

        assertEquals(1, bookA.getUnbalancedOrdersToBuySize());
        assertEquals(1, bookG.getUnbalancedOrdersToBuySize());

        engine.balance(bookA);
        engine.balance(bookG);

        assertEquals(1, bookA.getBalancedOrdersToBuySize());
        assertEquals(1, bookG.getBalancedOrdersToBuySize());
    }

    @Test
    void createBigBuyOrderAndManySmallSellOrders() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.SELL, 3, 3);
        controller.createOrder(createOrderRq0);
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.SELL, 2, 2);
        controller.createOrder(createOrderRq1);
        CreateOrderRq createOrderRq2 = new CreateOrderRq(GOOGL, Position.SELL, 2, 4);
        controller.createOrder(createOrderRq2);
        CreateOrderRq createOrderRq3 = new CreateOrderRq(GOOGL, Position.SELL, 1, 10);
        controller.createOrder(createOrderRq3);
        CreateOrderRq createOrderRq4 = new CreateOrderRq(GOOGL, Position.BUY, 3, 17);
        controller.createOrder(createOrderRq4);

        Book book = keeper.getBook(GOOGL);
        assertEquals(4, book.getUnbalancedOrdersToSellSize());
        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        engine.balance(book);
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());
    }

    @Test
    void createBigSellOrderAndManySmallBuyOrders() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.BUY, 3, 3);
        controller.createOrder(createOrderRq0);
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.BUY, 2, 2);
        controller.createOrder(createOrderRq1);
        CreateOrderRq createOrderRq2 = new CreateOrderRq(GOOGL, Position.BUY, 2, 4);
        controller.createOrder(createOrderRq2);
        CreateOrderRq createOrderRq3 = new CreateOrderRq(GOOGL, Position.BUY, 1, 10);
        controller.createOrder(createOrderRq3);
        CreateOrderRq createOrderRq4 = new CreateOrderRq(GOOGL, Position.SELL, 3, 17);
        controller.createOrder(createOrderRq4);

        Book book = keeper.getBook(GOOGL);
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(4, book.getUnbalancedOrdersToBuySize());
        engine.balance(book);
        assertEquals(1, book.getBalancedOrdersToSellSize());
        assertEquals(3, book.getBalancedOrdersToBuySize());
    }


    @Test
    void cancelOrder() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.BUY, 3, 3);
        Integer orderId = controller.createOrder(createOrderRq0).getId();
        Book book = keeper.getBook(GOOGL);
        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(1, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        CancelOrderRq cancelOrderRq0 = new CancelOrderRq(orderId, GOOGL, Position.BUY, 3, 3);
        controller.cancelOrder(cancelOrderRq0);

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());
    }

    @Test
    void cancel2BuyOrders() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.BUY, 1, 1);
        Integer orderId0 = controller.createOrder(createOrderRq0).getId();
        CreateOrderRq createOrderRq1 = new CreateOrderRq(GOOGL, Position.BUY, 3, 3);
        Integer orderId1 = controller.createOrder(createOrderRq1).getId();

        Book book = keeper.getBook(GOOGL);
        assertEquals(2, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(2, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        CancelOrderRq cancelOrderRq0 = new CancelOrderRq(orderId0, GOOGL, Position.BUY, 3, 3);
        controller.cancelOrder(cancelOrderRq0);

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(1, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        CancelOrderRq cancelOrderRq1 = new CancelOrderRq(orderId1, GOOGL, Position.BUY, 1, 1);
        controller.cancelOrder(cancelOrderRq1);

        assertEquals(1, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(1, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());
        engine.balance(book);

        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());
    }

    @Test
    void cancelWrongOrders() {
        CreateOrderRq createOrderRq0 = new CreateOrderRq(GOOGL, Position.SELL, 1, 1);
        controller.createOrder(createOrderRq0);

        Book book = keeper.getBook(GOOGL);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(0, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());

        CancelOrderRq cancelOrderRq0 = new CancelOrderRq( 4, GOOGL, Position.SELL, 3, 3);
        controller.cancelOrder(cancelOrderRq0);

        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(1, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());

        engine.balance(book);
        assertEquals(0, book.getUnbalancedOrdersToBuySize());
        assertEquals(0, book.getUnbalancedOrdersToSellSize());
        assertEquals(0, book.getBalancedOrdersToBuySize());
        assertEquals(1, book.getBalancedOrdersToSellSize());
    }

}