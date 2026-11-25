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
    private lateinit var amountInput: EditText
    private var pendingAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.statusText)
        amountInput = findViewById(R.id.amountInput)

        findViewById<Button>(R.id.btnPay).setOnClickListener {
            if (readAmount()) {
                status.text = "Ожидание карты..."
            }
        }

        findViewById<Button>(R.id.btnQr).setOnClickListener {
            if (readAmount()) {
                startActivity(
                    Intent(this, QrActivity::class.java)
                        .putExtra("amount", pendingAmount)
                )
            }
        }
    }

    private fun readAmount(): Boolean {
        val value = amountInput.text.toString().replace(',', '.')
        val amount = value.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            status.text = "Ошибка: введите сумму больше нуля"
            return false
        }
        pendingAmount = amount
        return true
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
            if (pendingAmount <= 0.0) {
                status.text = "Сначала введите сумму и нажмите кнопку NFC"
                return@runOnUiThread
            }
            val uid = tag.id.joinToString("") { "%02X".format(it) }
            val isPaymentCard = tag.techList.contains(IsoDep::class.java.name)

            if (!isPaymentCard) {
                status.text = "Это не платежная карта: ${tag.techList.joinToString()}"
                return@runOnUiThread
            }

            status.text = "Карта найдена\nUID: $uid\nПроверка..."
            status.text = PaymentSimulator.process(uid, pendingAmount)
        }
    }
}
