package com.example.switch_it.ui.theme

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.switch_it.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ConversionActivity : AppCompatActivity() {

    private lateinit var tvImageName: TextView
    private lateinit var btnConvert: Button

    private var imagePath: String? = null
    private var imageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion)

        // Initialize views
        tvImageName = findViewById(R.id.tv_image_name)
        btnConvert = findViewById(R.id.btn_convert)

        // Receive image name and path from MainActivity
        imageName = intent.getStringExtra("imageName")
        imagePath = intent.getStringExtra("imagePath")

        // Display the image name
        tvImageName.text = imageName ?: "No Image Selected"

        // Handle conversion action
        btnConvert.setOnClickListener {
            if (!imagePath.isNullOrEmpty()) {
                // Perform the conversion logic here (e.g., convert to PNG)
                convertImageToAnotherFormat(imagePath!!)
            }
        }
    }

    /**
     * Converts the image to another format (e.g., PNG) and saves it to the gallery
     * @param path The path to the image that needs to be converted
     */
    private fun convertImageToAnotherFormat(path: String) {
        // Assuming the input image is in JPG format and we want to convert it to PNG
        val convertedFile = convertImageToPNG(path)

        // Once conversion is done, you can update the UI or notify the user
        if (convertedFile != null) {
            // Inform the user the conversion was successful
            tvImageName.text = "Conversion successful: ${convertedFile.name}"

            // Save the converted file to the Android Gallery
            saveImageToGallery(convertedFile)
        } else {
            // Show error message if conversion fails
            tvImageName.text = "Error in conversion"
        }
    }

    /**
     * Convert the image to PNG format.
     * @param path The original image path
     * @return The converted file (if successful)
     */
    private fun convertImageToPNG(path: String): File? {
        val file = File(path)
        if (!file.exists()) {
            // Handle error if the file doesn't exist
            return null
        }

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        // Create a new file for the PNG format
        val outputDir = filesDir // Use app's internal files directory
        val outputFile = File(outputDir, "${file.nameWithoutExtension}.png")

        return try {
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            outputFile // Return the converted file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves the image to Android Gallery using MediaStore.
     * @param convertedFile The converted image file
     */
    private fun saveImageToGallery(convertedFile: File) {
        val contentResolver = contentResolver

        // Create a ContentValues object to store image data
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, convertedFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SwitchIT") // Save under a specific folder
        }

        // Insert the image into the MediaStore
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android Q (API level 29) and above, use MediaStore API for saving files
            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            imageUri?.let {
                try {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        val bitmap = BitmapFactory.decodeFile(convertedFile.absolutePath)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            // For below Android Q, use old method (write to external storage)
            val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), convertedFile.name)
            FileOutputStream(imageFile).use { outputStream ->
                val bitmap = BitmapFactory.decodeFile(convertedFile.absolutePath)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }
        }
    }
}
