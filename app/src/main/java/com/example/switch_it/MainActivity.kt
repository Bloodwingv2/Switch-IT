package com.example.switch_it
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val buttonConvert: Button = findViewById(R.id.buttonConvert)

        // Request storage permissions
        requestPermissions()

        // Initialize the ActivityResultLauncher for image picker
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Open image picker when the imageView is clicked
        imageView.setOnClickListener {
            openImagePicker()
        }

        // Convert the selected image when the button is clicked
        buttonConvert.setOnClickListener {
            imageUri?.let {
                convertImageToPng(it)
            } ?: Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to request permissions
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    // Function to open the image picker
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        pickImageLauncher.launch(intent)
    }

    // Function to convert JPEG to PNG
    private fun convertImageToPng(imageUri: Uri) {
        try {
            // Get the image as a bitmap
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            // Create a file to save the PNG in the public Pictures directory
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!picturesDir.exists()) {
                picturesDir.mkdirs() // Ensure the directory exists
            }
            val pngFile = File(picturesDir, "converted_image.png")

            // Write the PNG to the file
            val outputStream = FileOutputStream(pngFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            // Log the file creation
            Log.d("ImageConversion", "PNG file created: ${pngFile.absolutePath}")

            // Notify the media scanner about the new file
            MediaScannerConnection.scanFile(this, arrayOf(pngFile.absolutePath), null, null)

            Toast.makeText(this, "Image converted to PNG: ${pngFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show()
        }
    }
}
