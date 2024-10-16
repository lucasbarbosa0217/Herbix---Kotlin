package com.fiap.herbix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val responseBody = intent.getStringExtra("responseBody")
        Log.w("Response API", "onCreate: $responseBody", )

        val jsonObject = JSONObject(responseBody)
        val nomeCortada = jsonObject.getString("labelName")

        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        val titleResId: Int
        val descriptionResId: Int

        // Identificar o recurso string com base no nome da doença
        when (nomeCortada) {
            "Ácaro Rajado" -> {
                titleResId = R.string.acarorajado_name
                descriptionResId = R.string.acarorajado_content
            }
            "Cochonilhas" -> {
                titleResId = R.string.cochonilha_name
                descriptionResId = R.string.cochonilha_content
            }
            "Corós" -> {
                titleResId = R.string.coros_name
                descriptionResId = R.string.coros_content
            }
            "Formiga Cortadeira" -> {
                titleResId = R.string.formiga_cortadeira_name
                descriptionResId = R.string.formiga_cortadeira_content
            }
            "Helicoverpa armigera" -> {
                titleResId = R.string.helicoverpa_armigera_name
                descriptionResId = R.string.helicoverpa_armigera_content
            }
            "Lagarta do cartucho" -> {
                titleResId = R.string.lagarta_cartucho_name
                descriptionResId = R.string.lagarta_cartucho_content
            }
            "Larva minadora" -> {
                titleResId = R.string.lavaminadora_name
                descriptionResId = R.string.lavaminadora_content
            }
            "Mosca-branca" -> {
                titleResId = R.string.mosca_branca_name
                descriptionResId = R.string.mosca_branca_content
            }
            "Percevejo marrom" -> {
                titleResId = R.string.percebo_marrom_name
                descriptionResId = R.string.percebo_marrom_content
            }
            "Pulgões" -> {
                titleResId = R.string.pulgoes_name
                descriptionResId = R.string.pulgoes_content
            }
            else -> {
                titleResId = 0
                descriptionResId = 0
            }
        }
        if (titleResId != 0 && descriptionResId != 0) {
            titleTextView.setText(titleResId)
            descriptionTextView.setText(descriptionResId)
        } else{
            titleTextView.setText("Resposta não cadastrada")
            descriptionTextView.setText("descriptionResId")
        }



        val drawableResId = when (nomeCortada) {
            "Ácaro rajado" -> R.drawable.acarorajado
            "Cochonilhas" -> R.drawable.cochonilha
            "Corós" -> R.drawable.coros
            "Formiga Cortadeira" -> R.drawable.formigacortadeira
            "Helicoverpa armigera" -> R.drawable.helicoverpaarmigera
            "Lagarta do cartucho" -> R.drawable.lagartadocartucho
            "Larva minadora" -> R.drawable.larvaminadora
            "Mosca-branca" -> R.drawable.moscabranca
            "Percevejo marrom" -> R.drawable.percevejomarrom
            "Pulgões" -> R.drawable.pulgoes
            else -> 0
        }

        val imageView: ImageView = findViewById(R.id.imageView3)

        if (drawableResId != 0) {
            imageView.setImageResource(drawableResId)
        }else{
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
            finish()
        }

        val buttonView: Button = findViewById(R.id.button);

        buttonView.setOnClickListener { finish() }


    }
}