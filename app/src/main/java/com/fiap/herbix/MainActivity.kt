package com.fiap.herbix


import ApiCall
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var apiCall: ApiCall
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialize a instância da classe com.fiap.herbix.ApiCall
        apiCall = ApiCall()

        // Verifique e solicite as permissões necessárias
        checkPermissions()


        val btnProximaPagina = findViewById<Button>(R.id.btn_ia)
        val button = findViewById<Button>(R.id.button)

        // Exemplo de chamada para enviar a imagem quando o botão for clicado
        button.setOnClickListener {
            Log.d("API Response", "Click Listener On" ?: "")

            // Verifique se as permissões necessárias foram concedidas
            if (hasPermissions()) {
                Log.d("API Response", "Click Listener Tem Permissões" ?: "")

                // Exiba um diálogo de escolha para o usuário
                showImageSourceDialog()
            } else {
                Log.d("API Response", "Click Listener Não Tem Permissões" ?: "")

                // Solicite as permissões necessárias
                requestPermissions()
            }
        }

        btnProximaPagina.setOnClickListener {

            val intent = Intent(this, Gpt::class.java)
            startActivity(intent)

        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun hasPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        return readPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Câmera", "Galeria")
        val dialog = AlertDialog.Builder(this)
            .setTitle("Escolha a origem da imagem")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImageFromCamera()
                    1 -> selectImageFromGallery()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun captureImageFromCamera() {
        // Crie um arquivo para salvar a imagem
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
            ".jpg",
            storageDir
        )

        // Salve o caminho da foto na variável currentPhotoPath
        currentPhotoPath = imageFile.absolutePath

        // Crie o intent da câmera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoUri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        // Inicie a atividade da câmera
        resultLauncher.launch(intent)
    }


    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data

            if (selectedImageUri == null) {
                // Se a Uri for nula, provavelmente é uma foto da câmera
                // Nesse caso, você pode usar a Uri fornecida anteriormente para salvar a imagem em um arquivo
                val imageFile = File(currentPhotoPath)
                apiCall.uploadImage(imageFile)
            } else {
                // Se a Uri não for nula, é uma imagem da galeria
                // Você pode continuar com o código existente
                val imageFile = File(getRealPathFromUri(selectedImageUri))
                apiCall.uploadImage(imageFile)
            }
        }
    }


    private fun getRealPathFromUri(uri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 123
    }
}




