package models

class Cashier(val id: Int, private val bank: Bank) : Thread() {
    override fun run() {
        while (true) {
            val transaction = bank.transactionQueue.take()
            when (transaction.type) {
                TransactionType.DEPOSIT -> bank.deposit(transaction.clientId, transaction.amount)
                TransactionType.TRANSFER -> bank.transferFunds(transaction.senderId!!, transaction.receiverId!!, transaction.amount)
                TransactionType.EXCHANGE -> bank.exchangeCurrency(transaction.clientId, transaction.fromCurrency!!, transaction.toCurrency!!, transaction.amount)
            }
            Thread.sleep(500) // Симуляция задержки
        }
    }
}
