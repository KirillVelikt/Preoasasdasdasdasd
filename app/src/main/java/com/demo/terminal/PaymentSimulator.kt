package com.demo.terminal

import kotlin.random.Random

data class PaymentResult(
    val success: Boolean,
    val amount: Double,
    val rrn: String,
    val source: String,
    val detail: String
)

object PaymentSimulator {

    fun process(amount: Double, source: String, cardUid: String? = null): PaymentResult {
        val rrn = Random.nextInt(100000, 999999).toString()
        val success = Random.nextDouble() < 0.85 // 85% успехов для наглядности

        return if (success) {
            val detail = if (cardUid != null) "Карта (UID): $cardUid" else "Оплата по QR-коду"
            PaymentResult(true, amount, rrn, source, detail)
        } else {
            val reasons = listOf("insufficient_funds", "card_declined", "timeout")
            PaymentResult(false, amount, rrn, source, "Причина: ${reasons.random()}")
        }
    }
}
