package com.demo.terminal

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlin.random.Random

class QrActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        val amount = intent.getDoubleExtra("amount", 0.0)
        val amountText = findViewById<TextView>(R.id.qrAmountText)
        val qrImage = findViewById<ImageView>(R.id.qrImage)
        val qrStatus = findViewById<TextView>(R.id.qrStatus)
        amountText.text = "%.2f ₽".format(amount)

        // Демо-ссылка платежа: в реальном проекте здесь будет URL платежной системы
        val paymentId = "DEMO-" + Random.nextInt(100000, 999999)
        val payload = "https://demo.example/pay?id=$paymentId&amount=%.2f".format(amount)

        val bitmap: Bitmap? = QrGenerator.generate(payload, 512)
        if (bitmap != null) {
            qrImage.setImageBitmap(bitmap)
        } else {
            qrStatus.text = "Не удалось создать QR-код"
        }

        findViewById<Button>(R.id.btnConfirmQr).setOnClickListener {
            val rrn = Random.nextInt(100000, 999999).toString()
            val success = Random.nextBoolean()
            qrStatus.text = if (success) {
                "✅ ОПЛАТА ПРИНЯТА (демо)\nRRN: $rrn\nID платежа: $paymentId\nРеальное списание не выполнено"
            } else {
                "❌ ОТКАЗ (демо)\nПричина: insufficient_funds\nRRN: $rrn"
            }
        }
    }
}
