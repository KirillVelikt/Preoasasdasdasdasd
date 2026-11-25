package com.demo.terminal

import kotlin.random.Random

object PaymentSimulator {

    const val TERMINAL_ID = "24865373"

    data class Result(val success: Boolean, val rrn: String, val reason: String?)

    fun process(): Result {
        val rrn = Random.nextInt(100000, 999999).toString()
        // примерно 15% отказов, чтобы видеть оба сценария терминала
        val success = Random.nextInt(100) >= 15
        return if (success) {
            Result(true, rrn, null)
        } else {
            val reason = if (Random.nextBoolean()) "Недостаточно средств" else "Карта отклонена"
            Result(false, rrn, reason)
        }
    }
}
