# stock-market-sumulator

##Реализованы:
### клиент с обработкой команд из терминала
Для начала работы с клиентом можно написать "help" или "man"
Клиент поддерживает только запросы на создание заказов

### сервер с рест апи, есть сваггер
#### Запросы в сваггер:
CreateRq
- {
  "companySymbol": "GOOG",
  "position": "SELL",
  "amount": 1,
  "price": 1
}
- {
  "companySymbol": "GOOG",
  "position": "BUY",
  "amount": 1,
  "price": 1
}

CancelRq
- {
  "id": SOME_ID,
  "companySymbol": "GOOG",
  "position": "SELL",
  "amount": 1,
  "price": 1
  }
- {
  "id": SOME_ID,
  "companySymbol": "GOOG",
  "position": "BUY",
  "amount": 1,
  "price": 1
  }