package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity(), NfcAdapter.ReaderCallback {

    private lateinit var status: TextView
    private lateinit var amountText: TextView
    private lateinit var amountInput: EditText
    private var pendingAmount: Double = 0.0
    private var waitingForCard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        status = findViewById(R.id.statusText)
        amountText = findViewById(R.id.amountText)
        amountInput = findViewById(R.id.amountInput)

        if (NfcAdapter.getDefaultAdapter(this) == null) {
            status.text = "NFC недоступен на этом устройстве — доступна оплата по QR"
        }

        findViewById<Button>(R.id.btnNfcPay).setOnClickListener {
            pendingAmount = amountInput.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            if (pendingAmount <= 0.0) {
                status.text = "Введите сумму больше нуля"
                return@setOnClickListener
            }
            amountText.text = "%.2f ₽".format(pendingAmount)
            waitingForCard = true
            status.text = "Ожидание карты... Приложите карту к задней панели телефона"
        }

        findViewById<Button>(R.id.btnQrPay).setOnClickListener {
            val amount = amountInput.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            if (amount <= 0.0) {
                status.text = "Введите сумму больше нуля"
                return@setOnClickListener
            }
            amountText.text = "%.2f ₽".format(amount)
            startActivity(
                Intent(this, QrActivity::class.java)
                    .putExtra("amount", amount)
            )
        }
    }

    override fun onTagDiscovered(tag: Tag) {
        runOnUiThread {
            if (!waitingForCard) return@runOnUiThread

            val uid = tag.id.joinToString("") { "%02X".format(it) }
            val isPaymentCard = tag.techList.contains(IsoDep::class.java.name)

            if (!isPaymentCard) {
                status.text = "Это не платежная карта (${tag.techList.joinToString()})"
                return@runOnUiThread
            }

            status.text = "Карта найдена\nUID: $uid\nОбработка..."
            val result = PaymentSimulator.process(pendingAmount, "NFC", uid)
            waitingForCard = false
            showResult(result)
        }
    }

    fun showResult(result: PaymentResult) {
        if (result.success) {
            startActivity(
                Intent(this, SuccessActivity::class.java)
                    .putExtra("amount", result.amount)
                    .putExtra("rrn", result.rrn)
                    .putExtra("detail", result.detail)
            )
        } else {
            status.text = "❌ ОТКАЗ (демо)\n${result.detail}\nRRN: ${result.rrn}"
        }
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
}
