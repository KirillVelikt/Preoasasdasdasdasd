package com.demo.terminal

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlin.random.Random

class QrActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        val amount = intent.getDoubleExtra("amount", 0.0)
        findViewById<TextView>(R.id.qrAmount).text = "%.2f".format(amount)

        val payload = "https://demo.example.com/pay?amount=%.2f&term=%s"
            .format(amount, "TERM001")

        val size = 1024
        val matrix = QRCodeWriter().encode(
            payload, BarcodeFormat.QR_CODE, size, size,
            mapOf(EncodeHintType.MARGIN to 1)
        )
        val pixels = IntArray(size * size)
        for (y in 0 until size) for (x in 0 until size) {
            pixels[y * size + x] = if (matrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        findViewById<ImageView>(R.id.qrImage).setImageBitmap(bitmap)

        // Имитация подтверждения оплаты через 5 секунд
        findViewById<TextView>(R.id.qrStatus).text = "Покажите QR покупателю"
        android.os.Handler(mainLooper).postDelayed({
            val rrn = Random.nextInt(100000, 999999).toString()
            val ok = Random.nextBoolean()
            findViewById<TextView>(R.id.qrStatus).text = if (ok) {
                "OK: ОПЛАТА ПРИНЯТА (демо)\nRRN: $rrn\nРеальное списание не выполнено"
            } else {
                "ОТКАЗ (демо)\nRRN: $rrn"
            }
        }, 5000)
    }
}
