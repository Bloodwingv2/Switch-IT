package com.example.switch_it.ui.theme
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.switch_it.R

class ConversionActivity : AppCompatActivity() {

    private lateinit var tvImageName: TextView
    private lateinit var spinnerFormat: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion)

        // Initialize views
        tvImageName = findViewById(R.id.tv_image_name)
        spinnerFormat = findViewById(R.id.spinner_format)

        // Get image name from intent or other data source
        val imageName = intent.getStringExtra("IMAGE_NAME") ?: "No Image Selected"
        tvImageName.text = imageName

        // Set up spinner selection listener
        spinnerFormat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFormat = parent?.getItemAtPosition(position).toString()
                // Handle format selection if needed
                // For example, update UI or prepare conversion with selected format
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: handle no selection case if necessary
            }
        }
    }
}
