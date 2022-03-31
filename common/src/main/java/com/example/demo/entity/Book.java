package com.example.demo.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class Book {
    @Getter
    private final String symbols;
    private final AtomicInteger tradeIdGenerator;


    private List<Order> balancedOrdersToBuy = new ArrayList<>();
    private List<Order> balancedOrdersToSell = new ArrayList<>();

    private List<Order> unbalancedOrdersToBuy = new ArrayList<>();
    private List<Order> unbalancedOrdersToSell = new ArrayList<>();

    public synchronized void addOrder(Order order) {
        if (order.getPosition() == Position.BUY) {
            unbalancedOrdersToBuy.add(order);
        } else {
            unbalancedOrdersToSell.add(order);
        }
    }

    public synchronized boolean isEmpty() {
        return balancedOrdersToBuy.isEmpty() && balancedOrdersToSell.isEmpty()
                && unbalancedOrdersToBuy.isEmpty() && unbalancedOrdersToSell.isEmpty();
    }

    public synchronized List<Trade> balance() {
        cancelBalancedOrders();

        List<Order> mergedOrdersToBuy = balanceOrders(this.unbalancedOrdersToBuy, this.balancedOrdersToBuy);
        List<Order> mergedOrdersToSell = balanceOrders(this.unbalancedOrdersToSell, this.balancedOrdersToSell);

        List<Trade> trades = new ArrayList<>();
        if ((mergedOrdersToBuy != null && !mergedOrdersToBuy.isEmpty()) &&
                (mergedOrdersToSell != null && !mergedOrdersToSell.isEmpty())) {

            Order cheapestOrderToSell = mergedOrdersToSell.get(mergedOrdersToSell.size() - 1);
            Order orderToBuy = mergedOrdersToBuy.get(0);

            while (cheapestOrderToSell != null && orderToBuy != null
                    && cheapestOrderToSell.getPrice() <= orderToBuy.getPrice()) {
                int orderToSellAmount = cheapestOrderToSell.getAmount();
                int orderToBuyAmount = orderToBuy.getAmount();
                if (orderToSellAmount == orderToBuyAmount) {
                    mergedOrdersToSell.remove(cheapestOrderToSell);
                    mergedOrdersToBuy.remove(orderToBuy);

                    trades.add(new Trade(cheapestOrderToSell.getOrderId(), orderToBuy.getOrderId(),
                            tradeIdGenerator.getAndIncrement(), this.symbols,
                            orderToBuyAmount, cheapestOrderToSell.getPrice()));
                    if (mergedOrdersToSell.isEmpty() || mergedOrdersToBuy.isEmpty()) {
                        break;
                    }
                    cheapestOrderToSell = mergedOrdersToSell.get(mergedOrdersToSell.size() - 1);
                    orderToBuy = mergedOrdersToBuy.get(0);
                    continue;
                }
                if (orderToSellAmount > orderToBuyAmount) {
                    cheapestOrderToSell.setAmount(orderToSellAmount - orderToBuyAmount);
                    mergedOrdersToBuy.remove(orderToBuy);
                    trades.add(new Trade(cheapestOrderToSell.getOrderId(), orderToBuy.getOrderId(),
                            tradeIdGenerator.getAndIncrement(), this.symbols,
                            orderToBuyAmount, cheapestOrderToSell.getPrice()));
                    if (mergedOrdersToSell.isEmpty() || mergedOrdersToBuy.isEmpty()) {
                        break;
                    }
                    cheapestOrderToSell = mergedOrdersToSell.get(mergedOrdersToSell.size() - 1);
                    orderToBuy = mergedOrdersToBuy.get(0);
                    continue;
                }

                mergedOrdersToSell.remove(cheapestOrderToSell);
                orderToBuy.setAmount(orderToBuyAmount - orderToSellAmount);
                trades.add(new Trade(cheapestOrderToSell.getOrderId(), orderToBuy.getOrderId(),
                        tradeIdGenerator.getAndIncrement(), this.symbols,
                        cheapestOrderToSell.getAmount(), cheapestOrderToSell.getPrice()));

                if (mergedOrdersToSell.isEmpty() || mergedOrdersToBuy.isEmpty()) {
                    break;
                }
                cheapestOrderToSell = mergedOrdersToSell.get(mergedOrdersToSell.size() - 1);
                orderToBuy = mergedOrdersToBuy.get(0);
            }
        }

        return trades;
    }

    private List<Order> balanceOrders(List<Order> unbalancedOrders, List<Order> balancedOrders) {
        List<Order> mergedOrders;
        List<Order> unbalancedCreateOrders = unbalancedOrders.stream()
                .filter(el -> el.getType().equals(OrderType.CREATE))
                .collect(Collectors.toList());
        if (!unbalancedCreateOrders.isEmpty()) {
            mergedOrders = mergeListsAndSort(balancedOrders, unbalancedCreateOrders);
            unbalancedOrders.clear();
        } else {
            mergedOrders = balancedOrders;
        }
        return mergedOrders;
    }

    private void cancelBalancedOrders() {
        this.balancedOrdersToBuy = cancelOrders(this.unbalancedOrdersToBuy, this.balancedOrdersToBuy);
        this.unbalancedOrdersToBuy = this.unbalancedOrdersToBuy.stream()
                .collect(Collectors.groupingBy(Order::getType)).getOrDefault(OrderType.CREATE, new ArrayList<>());

        this.balancedOrdersToSell = cancelOrders(this.unbalancedOrdersToSell, this.balancedOrdersToSell);
        this.unbalancedOrdersToSell = this.unbalancedOrdersToSell.stream()
                .collect(Collectors.groupingBy(Order::getType)).getOrDefault(OrderType.CREATE, new ArrayList<>());
    }

    private List<Order> cancelOrders(List<Order> unbalancedOrders, List<Order> balancedOrders) {
        List<Order> result = new ArrayList<>();

        List<Order> cancelOrders = unbalancedOrders.stream()
                .collect(Collectors.groupingBy(Order::getType)).getOrDefault(OrderType.CANCEL, new ArrayList<>());

        if (!cancelOrders.isEmpty()) {
            for (Order balancedOrder : balancedOrders) {
                boolean isCanceled = false;
                for (Order cancelOrder : cancelOrders) {
                    if (orderHasCancelled(balancedOrder, cancelOrder)) {
                        isCanceled = true;
                        break;
                    }
                }
                if (!isCanceled) {
                    result.add(balancedOrder);
                }
            }
        } else {
            return balancedOrders;
        }
        return result;
    }

    private boolean orderHasCancelled(Order balancedOrder, Order cancelOrder) {
        return balancedOrder.getPosition().equals(cancelOrder.getPosition())
                && balancedOrder.getCompanySymbols().equals(cancelOrder.getCompanySymbols())
                && balancedOrder.getPrice() == cancelOrder.getPrice()
                && balancedOrder.getAmount() == cancelOrder.getAmount();
    }

    private List<Order> mergeListsAndSort(List<Order> balanced, List<Order> unbalanced) {
        balanced.addAll(unbalanced);
        balanced.sort(Order::compareTo);
        return balanced;
    }


    public Integer getBalancedOrdersToBuySize() {
        return this.balancedOrdersToBuy.size();
    }

    public Integer getBalancedOrdersToSellSize() {
        return this.balancedOrdersToSell.size();
    }

    public Integer getUnbalancedOrdersToBuySize() {
        return this.unbalancedOrdersToBuy.size();
    }

    public Integer getUnbalancedOrdersToSellSize() {
        return this.unbalancedOrdersToSell.size();
    }


}
