package com.example.switch_it

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var tvHistory: TextView? = null
    private var historyCount = 1 // Starting count for history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAddImage = findViewById<Button>(R.id.btn_add_image)
        tvHistory = findViewById(R.id.tv_history)

        // Set the initial history count
        updateHistoryCount()

        // Button click listener to add an image
        btnAddImage.setOnClickListener {
            // Logic to add an image
            historyCount++ // Increment the history count
            updateHistoryCount() // Update the UI
        }
    }

    // Method to update the history text
    private fun updateHistoryCount() {
        tvHistory!!.text = "HISTORY ($historyCount)"
    }
}