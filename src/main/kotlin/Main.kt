import models.Bank
import models.Cashier
import models.Transaction
import models.TransactionType
import observers.Logger

fun main() {
    val bank = Bank()
    bank.addObserver(Logger()) // Добавляем логгер как наблюдателя

    // Создаём клиентов
    val client1 = bank.createClient(balance = 1000.0, currency = "USD")
    val client2 = bank.createClient(balance = 2000.0, currency = "EUR")

    // Запускаем кассы
    val cashier1 = Cashier(1, bank)
    val cashier2 = Cashier(2, bank)
    cashier1.start()
    cashier2.start()

    // Пример транзакций
    bank.addTransaction(Transaction(TransactionType.DEPOSIT, client1.id, amount = 500.0))
    bank.addTransaction(Transaction(TransactionType.TRANSFER, client1.id, client2.id, 100.0))
    bank.addTransaction(Transaction(TransactionType.EXCHANGE, client1.id, amount = 200.0, fromCurrency = "USD", toCurrency = "EUR"))

    Thread.sleep(5000) // Ждём, пока кассы обработают транзакции
}
