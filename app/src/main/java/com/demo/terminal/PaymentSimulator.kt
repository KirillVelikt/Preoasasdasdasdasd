package com.demo.terminal

import kotlin.random.Random

object PaymentSimulator {

    fun process(cardUid: String, amount: Double): String {
        if (amount <= 0.0) return "Ошибка: сумма не задана"

        Thread.sleep(800) // имитация обращения к процессингу
        val rrn = Random.nextInt(100000, 999999).toString()
        val success = Random.nextBoolean()

        return if (success) {
            "✅ ОПЛАТА ПРИНЯТА (демо)\n" +
                "Сумма: %.2f ₽\n".format(amount) +
                "Карта (UID): $cardUid\n" +
                "RRN: $rrn\nРеальное списание не выполнено"
        } else {
            "❌ ОТКАЗ (демо)\n" +
                "Причина: insufficient_funds\n" +
                "RRN: $rrn"
        }
    }
}
