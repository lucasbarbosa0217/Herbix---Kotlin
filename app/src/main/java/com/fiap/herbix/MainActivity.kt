package com.fiap.herbix

import ApiCall
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
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
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiCall = ApiCall()
        progressDialog = ProgressDialog(this).apply {
            setMessage("Carregando...")
            setCancelable(false)
        }

        checkPermissions()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            if (hasPermissions()) {
                showImageSourceDialog()
            } else {
                requestPermissions()
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun hasPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSIONS_REQUEST_CODE)
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Câmera", "Galeria")
        AlertDialog.Builder(this)
            .setTitle("Escolha a origem da imagem")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImageFromCamera()
                    1 -> selectImageFromGallery()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun captureImageFromCamera() {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
            ".jpg",
            storageDir
        )

        currentPhotoPath = imageFile.absolutePath
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoUri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

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
            progressDialog.show()

            if (selectedImageUri == null) {
                val imageFile = File(currentPhotoPath)
                apiCall.uploadImage(this, imageFile) { success ->
                    runOnUiThread {
                        progressDialog.dismiss()
                        if (success) {
                            Toast.makeText(this, "Imagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Falha ao enviar imagem.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                val imageFile = File(getRealPathFromUri(selectedImageUri))
                apiCall.uploadImage(this, imageFile) { success ->
                    runOnUiThread {
                        progressDialog.dismiss()
                        if (success) {
                            Toast.makeText(this, "Imagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Falha ao enviar imagem.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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

    override fun onRestart() {
        super.onRestart()
        progressDialog.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showImageSourceDialog()
            } else {
                Toast.makeText(this, "O aplicativo não funcionará corretamente sem as permissões necessárias.", Toast.LENGTH_LONG).show()

                if (permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
                    Toast.makeText(this, "Permissões necessárias para acessar a câmera e galeria", Toast.LENGTH_SHORT).show()
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("As permissões são necessárias para o funcionamento do aplicativo. Vá até as configurações para concedê-las manualmente.")
                        .setPositiveButton("Configurações") { _, _ ->
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }
        }
    }
}
