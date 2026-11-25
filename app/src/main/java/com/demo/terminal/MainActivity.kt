package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.amountInput)
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            val value = input.text.toString().replace(',', '.').toDoubleOrNull()
            if (value == null || value <= 0.0) {
                Toast.makeText(this, "Введите сумму больше нуля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("amount", value)
            startActivity(intent)
        }
    }
}
