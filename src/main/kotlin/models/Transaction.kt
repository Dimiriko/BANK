package models

enum class TransactionType { DEPOSIT, WITHDRAW, TRANSFER, EXCHANGE }

data class Transaction(
    val type: TransactionType,
    val clientId: Int,
    val amount: Double,
    val senderId: Int? = null,
    val receiverId: Int? = null,
    val fromCurrency: String? = null,
    val toCurrency: String? = null
)
