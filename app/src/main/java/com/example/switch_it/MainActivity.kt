package com.example.switch_it

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val buttonConvert: Button = findViewById(R.id.buttonConvert)

        // Image picker initialization
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri!!))
                imageView.setImageBitmap(bitmap)
            }
        }

        // OnClickListener to open image picker
        imageView.setOnClickListener {
            openImagePicker()
        }

        // Button to convert image
        buttonConvert.setOnClickListener {
            imageUri?.let {
                convertImageToPng(it)
            } ?: Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun convertImageToPng(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val picturesDir = File(getExternalFilesDir(null), "Pictures")
            if (!picturesDir.exists()) picturesDir.mkdirs()
            val pngFile = File(picturesDir, "converted_image.png")

            val outputStream = FileOutputStream(pngFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            Toast.makeText(this, "Image converted to PNG: ${pngFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show()
        }
    }
}
