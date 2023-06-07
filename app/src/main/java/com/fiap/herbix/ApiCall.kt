import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.fiap.herbix.ResultActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.math.log

class ApiCall {

    fun uploadImage(context: Context, imageFile: File, callback: (Boolean) -> Unit) {
        val client = OkHttpClient()

        getToken { token ->
            if (token != null) {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "image",
                        imageFile.name,
                        RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
                    )
                    .build()
                Log.w("Response API", token )


                    val jsonObject = JSONObject(token)
                     val tokenCortada = jsonObject.getString("access_token")



                val request = Request.Builder()
                    .url("https://www.nyckel.com/v1/functions/j4rq6vb5veq00lq6/invoke")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer "+tokenCortada)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Manipule a falha na solicitação aqui
                        Log.e("API Response", "Request failed: ${e.message}")
                        Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        // Manipule a resposta da solicitação aqui
                        Log.d("API Response", "Response: $responseBody")
                        // Cria uma Intent

                        val intent = Intent(context, ResultActivity::class.java)
                        intent.putExtra("responseBody", responseBody)

                        // Inicia a ResultActivity
                        context.startActivity(intent)
                    }
                })
            } else {
                // Lógica para lidar com falha ao obter o token
                Log.e("API Response", "Failed to get token")
            }
        }

    }
    fun getToken(callback: (String?) -> Unit) {
        val clientId = "a7jbecjr9hpfui0yui1wijijtfv42y1v"
        val clientSecret = "lgfjb05ju7yvmbtvcxc67alqo0hzheoaftte2gntch2blmxhlzs5mvoauovbj62f"
        val grantType = "client_credentials"

        val formBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("grant_type", grantType)
            .build()

        val request = Request.Builder()
            .url("https://www.nyckel.com/connect/token")
            .post(formBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                callback(responseBody)
            }
        })
    }



}
