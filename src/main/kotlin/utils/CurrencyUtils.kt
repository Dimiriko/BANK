package utils

object CurrencyUtils {
    fun getRandomExchangeRate(): Double {
        return (0.8..1.2).random() // Случайный курс валют в диапазоне от 0.8 до 1.2
    }
}
