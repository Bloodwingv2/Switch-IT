package com.example.switch_it

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.switch_it.ui.theme.ConversionActivity

class MainActivity : AppCompatActivity() {
    private var tvHistory: TextView? = null
    private var historyCount = 1 // Starting count for history
    private val PICK_IMAGES_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appTitle: TextView = findViewById(R.id.app_title)
        val btnAddImage = findViewById<Button>(R.id.btn_add_image)
        tvHistory = findViewById(R.id.tv_history)

        // Create a SpannableString with the text "Switch-IT"
        val spannable = SpannableString("Switch-IT")
        val redColor = ContextCompat.getColor(this, R.color.red)
        val yellowColor = ContextCompat.getColor(this, R.color.yellow)

        // Apply different colors to specific letters
        spannable.setSpan(ForegroundColorSpan(redColor), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // S to red
        spannable.setSpan(ForegroundColorSpan(yellowColor), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // W to yellow
        spannable.setSpan(ForegroundColorSpan(redColor), 8, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // T to red

        // Set the SpannableString to the TextView
        appTitle.text = spannable

        // Set the initial history count
        updateHistoryCount()

        // Button click listener to add an image
        btnAddImage.setOnClickListener {
            // Open file manager to select images
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // Allows multiple image selection
            startActivityForResult(intent, PICK_IMAGES_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUris = ArrayList<Uri>()
            val imageNames = ArrayList<String>() // To store image names
            val imagePaths = ArrayList<String>() // To store image paths

            if (data.clipData != null) { // Multiple images selected
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageUris.add(imageUri)

                    // Retrieve image name and path
                    val imageName = getImageName(imageUri)
                    imageNames.add(imageName)

                    val imagePath = getImagePathFromUri(imageUri)
                    imagePaths.add(imagePath)
                }
            } else if (data.data != null) { // Single image selected
                val imageUri = data.data!!
                imageUris.add(imageUri)

                // Retrieve image name and path
                val imageName = getImageName(imageUri)
                imageNames.add(imageName)

                val imagePath = getImagePathFromUri(imageUri)
                imagePaths.add(imagePath)
            }

            // Increment history count and update UI
            historyCount++
            updateHistoryCount()

            // Pass both the image name and image path to ConversionActivity
            val intent = Intent(this, ConversionActivity::class.java)
            if (imageNames.isNotEmpty()) {
                intent.putExtra("imageName", imageNames[0]) // Pass the first image name
            } else {
                intent.putExtra("imageName", "No Image Selected")
            }
            if (imagePaths.isNotEmpty()) {
                intent.putExtra("imagePath", imagePaths[0]) // Pass the first image path
            } else {
                intent.putExtra("imagePath", "No Image Selected")
            }
            startActivity(intent)
        }
    }

    // Method to retrieve image name from Uri
    private fun getImageName(uri: Uri): String {
        var imageName = "Unknown"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                if (nameIndex != -1) {
                    imageName = it.getString(nameIndex)
                }
            }
        }
        return imageName
    }

    // Method to retrieve image path from Uri
    private fun getImagePathFromUri(uri: Uri): String {
        var imagePath = "Unknown"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (columnIndex != -1) {
                    imagePath = it.getString(columnIndex)
                }
            }
        }
        return imagePath
    }

    // Method to update the history text
    private fun updateHistoryCount() {
        tvHistory!!.text = "HISTORY ($historyCount)"
    }
}
