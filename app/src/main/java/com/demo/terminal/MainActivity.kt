package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity(), NfcAdapter.ReaderCallback {

    private lateinit var status: TextView
    private lateinit var amountText: TextView
    private lateinit var amountInput: EditText
    private var pendingAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.statusText)
        amountText = findViewById(R.id.amountText)
        amountInput = findViewById(R.id.amountInput)

        findViewById<Button>(R.id.btnNfc).setOnClickListener {
            val v = amountInput.text.toString().replace(',', '.')
            pendingAmount = v.toDoubleOrNull() ?: 0.0
            if (pendingAmount <= 0.0) {
                status.text = "Введите сумму"
            } else {
                amountText.text = "%.2f".format(pendingAmount)
                status.text = "Поднесите карту к задней панели..."
                enableNfc()
            }
        }

        findViewById<Button>(R.id.btnQr).setOnClickListener {
            val v = amountInput.text.toString().replace(',', '.')
            val amount = v.toDoubleOrNull() ?: 0.0
            if (amount <= 0.0) {
                status.text = "Введите сумму"
            } else {
                startActivity(
                    Intent(this, QrActivity::class.java)
                        .putExtra("amount", amount)
                )
            }
        }
    }

    private fun enableNfc() {
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

    override fun onResume() {
        super.onResume()
        if (pendingAmount > 0.0) enableNfc()
    }

    override fun onTagDiscovered(tag: Tag) {
        runOnUiThread {
            val uid = tag.id.joinToString("") { "%02X".format(it) }
            val isPaymentCard = tag.techList.contains("android.nfc.tech.IsoDep")
            if (!isPaymentCard) {
                status.text = "Это не платежная карта"
            } else {
                status.text = PaymentSimulator.process(uid, pendingAmount)
            }
        }
    }
}
