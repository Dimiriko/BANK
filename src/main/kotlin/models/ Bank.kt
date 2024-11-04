package models

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import observers.Observer
import utils.CurrencyUtils

class Bank {
    private val clients = ConcurrentHashMap<Int, Client>()
    private val cashiers = mutableListOf<Cashier>()
    private val observers = mutableListOf<Observer>()
    val transactionQueue = LinkedBlockingQueue<Transaction>()
    private val exchangeRates = ConcurrentHashMap<String, Double>()

    init {
        // Инициализируем курсы валют и обновляем их каждые 1 час
        val executor = ScheduledThreadPoolExecutor(1)
        executor.scheduleAtFixedRate({
            exchangeRates["USD"] = CurrencyUtils.getRandomExchangeRate()
            exchangeRates["EUR"] = CurrencyUtils.getRandomExchangeRate()
            notifyObservers("Exchange rates updated.")
        }, 0, 1, TimeUnit.HOURS)
    }

    fun createClient(balance: Double, currency: String): Client {
        val id = clients.size + 1
        val client = Client(id, balance, currency)
        clients[id] = client
        notifyObservers("Client $id created with balance $balance $currency.")
        return client
    }

    fun addTransaction(transaction: Transaction) {
        transactionQueue.put(transaction)
        notifyObservers("Transaction added: $transaction")
    }

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun notifyObservers(message: String) {
        observers.forEach { it.update(message) }
    }

    // Метод для депозита средств
    fun deposit(clientId: Int, amount: Double) {
        val client = clients[clientId]
        if (client != null) {
            synchronized(client) {
                client.balance += amount
                notifyObservers("Deposit successful for client $clientId, new balance: ${client.balance}")
            }
        } else {
            notifyObservers("Deposit failed: Client $clientId not found.")
        }
    }

    // Метод для перевода средств между клиентами
    fun transferFunds(senderId: Int, receiverId: Int, amount: Double) {
        val sender = clients[senderId]
        val receiver = clients[receiverId]

        if (sender != null && receiver != null && sender.balance >= amount) {
            synchronized(sender) {
                synchronized(receiver) {
                    sender.balance -= amount
                    receiver.balance += amount
                    notifyObservers("Transfer of $amount from client $senderId to client $receiverId successful. New balances: Sender - ${sender.balance}, Receiver - ${receiver.balance}")
                }
            }
        } else {
            notifyObservers("Transfer failed: Check clients and balance.")
        }
    }

    // Метод для обмена валют
    fun exchangeCurrency(clientId: Int, fromCurrency: String, toCurrency: String, amount: Double) {
        val client = clients[clientId]
        if (client != null && client.currency == fromCurrency && client.balance >= amount) {
            val exchangeRate = exchangeRates[toCurrency] ?: 1.0
            val convertedAmount = amount * exchangeRate

            synchronized(client) {
                client.balance -= amount
                client.balance += convertedAmount
                client.currency = toCurrency
                notifyObservers("Currency exchange for client $clientId successful. New balance: ${client.balance} $toCurrency")
            }
        } else {
            notifyObservers("Currency exchange failed: Insufficient funds or mismatched currency.")
        }
    }
}
