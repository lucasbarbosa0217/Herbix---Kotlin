package com.fiap.herbix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fiap.herbix.data.HomeViewModel
import com.fiap.herbix.databinding.ActivityGptBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class Gpt : AppCompatActivity() {
    private var binding: ActivityGptBinding? = null
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGptBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnSearch?.setOnClickListener {
            val inputMessage: String = binding?.etTextInputUser?.text.toString()

            if (inputMessage.isNotEmpty()){
                makeRequestToChatGPT(inputMessage)
            }else {
                Toast.makeText(this@Gpt, "Pesquisa Inválida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeRequestToChatGPT(message: String){
        lifecycleScope.launch {
            viewModel.getApiResponse(message)
            viewModel.apiResponse.onEach { response ->
                if (response != null){
                    binding?.tvResponseChatGpt?.text = response
                }
            }.launchIn(lifecycleScope)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}