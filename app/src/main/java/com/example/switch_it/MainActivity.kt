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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var tvHistory: TextView? = null
    private var historyCount = 1 // Starting count for history

    // Define the ActivityResultLauncher
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleImageSelection(it)
        }
    }

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
            // Open file manager to select an image
            pickImageLauncher.launch("image/*")
        }
    }

    private fun handleImageSelection(uri: Uri) {
        val imageName = getImageName(uri)
        val imagePath = getImagePathFromUri(uri)

        // Increment history count and update UI
        historyCount++
        updateHistoryCount()

        // Pass both the image name and image path to ConversionActivity
        val intent = Intent(this, ConversionActivity::class.java)
        intent.putExtra("imageName", imageName)
        intent.putExtra("imagePath", imagePath)
        startActivity(intent)
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
