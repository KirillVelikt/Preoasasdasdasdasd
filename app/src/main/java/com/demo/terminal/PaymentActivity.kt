package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class PaymentActivity : Activity(), NfcAdapter.ReaderCallback {

    private var amount = 0.0
    private var done = false
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        amount = intent.getDoubleExtra("amount", 0.0)
        statusText = findViewById(R.id.statusText)

        findViewById<TextView>(R.id.amountText).text =
            String.format("%.2f", amount).replace('.', ',') + " ₽"

        val qrImage = findViewById<ImageView>(R.id.qrImage)
        val payload = "yumani-demo://pay?terminal=" + PaymentSimulator.TERMINAL_ID +
                "&amount=" + String.format("%.2f", amount)
        qrImage.setImageBitmap(QrGenerator.generate(payload, 600))

        findViewById<Button>(R.id.btnSmile).setOnClickListener { pay("QR-код") }
        findViewById<ImageButton>(R.id.btnClose).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        NfcAdapter.getDefaultAdapter(this)?.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        NfcAdapter.getDefaultAdapter(this)?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag) {
        runOnUiThread {
            if (done) return@runOnUiThread

            val isPaymentCard = tag.techList.contains(IsoDep::class.java.name)
            if (!isPaymentCard) {
                statusText.text = "Это не платёжная карта, поднесите банковскую карту"
                return@runOnUiThread
            }

            val uid = tag.id.joinToString("") { "%02X".format(it) }
            statusText.text = "Карта считана: $uid"
            pay("NFC", uid)
        }
    }

    private fun pay(method: String, uid: String = "") {
        if (done || amount <= 0.0) return
        done = true

        val result = PaymentSimulator.process()

        val intent = Intent(this, SuccessActivity::class.java)
            .putExtra("amount", amount)
            .putExtra("rrn", result.rrn)
            .putExtra("method", method)
            .putExtra("uid", uid)
            .putExtra("success", result.success)
            .putExtra("reason", result.reason ?: "")
        startActivity(intent)
        finish()
    }
}
