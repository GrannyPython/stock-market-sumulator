Симулятор биржи.

Написаны:
- сервер с рест апи, есть сваггер
Валидные запросы:
CreateRq
{
  "companySymbol": "string",
  "position": "SELL",
  "amount": 1,
  "price": 1
}
{
  "companySymbol": "string",
  "position": "BUY",
  "amount": 1,
  "price": 1
}

CancelRq

- клиент с обработкой команд из терминаоа