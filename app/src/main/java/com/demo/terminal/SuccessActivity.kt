package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView

class SuccessActivity : Activity() {

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val amount = intent.getDoubleExtra("amount", 0.0)
        val rrn = intent.getStringExtra("rrn") ?: ""
        val detail = intent.getStringExtra("detail") ?: ""

        findViewById<TextView>(R.id.receiptText).text =
            "Сумма: %.2f ₽\n%s\nRRN: %s\n\nРеальное списание не выполнено\n(демо-режим)"
                .format(amount, detail, rrn)

        // Звук успеха
        player = MediaPlayer.create(this, R.raw.success)
        player?.start()

        // Автовозврат на главный экран через 6 секунд
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }, 6000)
    }

    override fun onDestroy() {
        player?.release()
        super.onDestroy()
    }
}
