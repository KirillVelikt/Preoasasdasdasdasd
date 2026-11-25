package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QrActivity : Activity() {

    private lateinit var timerText: TextView
    private lateinit var qrImage: ImageView
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        val amount = intent.getDoubleExtra("amount", 0.0)
        qrImage = findViewById(R.id.qrImage)
        timerText = findViewById(R.id.qrTimer)

        findViewById<TextView>(R.id.qrAmount).text = "%.2f ₽".format(amount)

        val paymentId = UUID.randomUUID().toString().substring(0, 8)
        val ts = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val payload = "yumani-demo://pay?id=$paymentId&amount=${"%.2f".format(amount)}&ts=$ts"

        showQr(payload)

        // Срок жизни QR — 5 минут
        timer = object : CountDownTimer(5 * 60 * 1000L, 1000) {
            override fun onTick(ms: Long) {
                val m = ms / 60000
                val s = (ms / 1000) % 60
                timerText.text = "Код действителен: %d:%02d".format(m, s)
            }
            override fun onFinish() {
                finish()
            }
        }.start()

        findViewById<Button>(R.id.btnConfirmQr).setOnClickListener {
            timer?.cancel()
            val result = PaymentSimulator.process(amount, "QR")
            if (result.success) {
                startActivity(
                    Intent(this, SuccessActivity::class.java)
                        .putExtra("amount", result.amount)
                        .putExtra("rrn", result.rrn)
                        .putExtra("detail", result.detail)
                )
                finish()
            } else {
                timerText.text = "❌ ОТКАЗ (демо): ${result.detail}"
            }
        }

        findViewById<Button>(R.id.btnCancelQr).setOnClickListener {
            timer?.cancel()
            finish()
        }
    }

    private fun showQr(payload: String) {
        val writer = QRCodeWriter()
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val matrix = writer.encode(payload, BarcodeFormat.QR_CODE, 600, 600, hints)
        val bmp = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565)
        for (x in 0 until 600) {
            for (y in 0 until 600) {
                bmp.setPixel(x, y, if (matrix[x, y]) 0xFF212121.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        qrImage.setImageBitmap(bmp)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
