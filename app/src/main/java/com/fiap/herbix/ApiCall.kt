import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException
import kotlin.math.log

class ApiCall {

 fun uploadImage(imageFile: File) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", imageFile.name, RequestBody.create("image/*".toMediaTypeOrNull(), imageFile))
            .build()

        val request = Request.Builder()
            .url("https://www.nyckel.com/v1/functions/j4rq6vb5veq00lq6/invoke")
            .post(requestBody)
            .addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6ImF0K2p3dCJ9.eyJuYmYiOjE2ODU1NzkyODEsImV4cCI6MTY4NTU4Mjg4MSwiaXNzIjoiaHR0cHM6Ly93d3cubnlja2VsLmNvbSIsImNsaWVudF9pZCI6ImE3amJlY2pyOWhwZnVpMHl1aTF3aWppanRmdjQyeTF2IiwianRpIjoiQkQxQUIwREZGNjFGOTlCQkQwNjU2NTU4QUY1QTQ3MkQiLCJpYXQiOjE2ODU1NzkyODEsInNjb3BlIjpbImFwaSJdfQ.IQlZnFypE3Ury2zSgf8AvKmNfk73Y7e45XgE_W6nOQl5_qC9TUdqOaT6RkRqgMgry_L65bIn1okpauCMygCI-nCQqpEypNMOmT-4Z4fhM450INDprIFYKoiho1tyRxGbNcAQ-oH_VhbTqQHAthyrcLMcGnSYedKxvJO-CWE96wVcKH4qXUY1nqIa4e4t9hFl01Fa2vfAp12HmklcbZys-VrOJph0xIWYZoARPeRy2uApGdFpOXRjCei8hn2S7oyixRejD0stNceU0ufVxkA4-yQy67x79KAbo8a8jEAhXNE9Bc3uMxS6eQhG5LT4dsitmZGac1SIOVjQ3A4Su9Bkjw")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manipule a falha na solicitação aqui
                Log.e("API Response", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                // Manipule a resposta da solicitação aqui
                Log.d("API Response", "Response: $responseBody")
            }
        })
    }

}
