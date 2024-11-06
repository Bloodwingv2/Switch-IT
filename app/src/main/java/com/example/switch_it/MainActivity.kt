package com.example.switch_it

import ConversionActivity
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
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allows multiple image selection
            startActivityForResult(intent, PICK_IMAGES_REQUEST)
        }
        // Assuming you have the selected image name (e.g., selectedImageName)
        val selectedImageName = "example_image.png" // Replace with actual image name or path

        // Create an Intent to start ConversionActivity
                val intent = Intent(this, ConversionActivity::class.java)

        // Pass the image name as an extra
                intent.putExtra("IMAGE_NAME", selectedImageName)

        // Start ConversionActivity
                startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUris = ArrayList<Uri>()

            if (data.clipData != null) { // Multiple images selected
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else if (data.data != null) { // Single image selected
                val imageUri = data.data!!
                imageUris.add(imageUri)
            }

            // Increment history count and update UI
            historyCount++
            updateHistoryCount()

            // Pass the selected images to the ConversionActivity
            val intent = Intent(this, ConversionActivity::class.java)
            intent.putParcelableArrayListExtra("imageUris", imageUris)
            startActivity(intent)
        }
    }

    // Method to update the history text
    private fun updateHistoryCount() {
        tvHistory!!.text = "HISTORY ($historyCount)"
    }
}
