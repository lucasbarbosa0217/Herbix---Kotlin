package com.fiap.herbix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnProximaPagina = findViewById<Button>(R.id.btn_ia)
        val button = findViewById<Button>(R.id.button)

        btnProximaPagina.setOnClickListener {

            val intent = Intent(this, Gpt::class.java)
            startActivity(intent)

        }

        button.setOnClickListener {

            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)

        }

    }

}
