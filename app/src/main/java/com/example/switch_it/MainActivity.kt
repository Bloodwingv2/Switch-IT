package com.example.switch_it
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private var tvHistory: TextView? = null
    private var historyCount = 1 // Starting count for history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val appTitle: TextView = findViewById(R.id.app_title)

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