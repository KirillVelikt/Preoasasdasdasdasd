package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView

class SuccessActivity : Activity() {

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val success = intent.getBooleanExtra("success", true)
        val amount = intent.getDoubleExtra("amount", 0.0)
        val rrn = intent.getStringExtra("rrn") ?: ""
        val method = intent.getStringExtra("method") ?: ""
        val uid = intent.getStringExtra("uid") ?: ""
        val reason = intent.getStringExtra("reason") ?: ""

        val title = findViewById<TextView>(R.id.titleText)
        val details = findViewById<TextView>(R.id.detailsText)
        val money = String.format("%.2f", amount).replace('.', ',') + " ₽"

        if (success) {
            title.text = "ОПЛАТА ПРИНЯТА"
            findViewById<TextView>(R.id.amountText).text = money
            details.text = buildString {
                appendLine("RRN: $rrn")
                appendLine("Способ: $method")
                if (uid.isNotEmpty()) appendLine("Карта: $uid")
                append("Демо-режим: списание не выполнено")
            }
            player = MediaPlayer.create(this, R.raw.success)
        } else {
            title.text = "ОТКАЗ"
            findViewById<TextView>(R.id.amountText).text = money
            details.text = buildString {
                appendLine("Причина: " + reason.ifEmpty { "операция отклонена" })
                append("Попробуйте другую карту или QR-код")
            }
            findViewById<LinearLayout>(R.id.rootView).setBackgroundResource(R.drawable.bg_fail)
            player = MediaPlayer.create(this, R.raw.fail)
        }
        player?.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val back = Intent(this, MainActivity::class.java)
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(back)
            finish()
        }, if (success) 6000L else 4000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
