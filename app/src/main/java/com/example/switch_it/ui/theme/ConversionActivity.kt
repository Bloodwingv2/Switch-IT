package com.example.switch_it.ui.theme

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.switch_it.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ConversionActivity : AppCompatActivity() {

    private lateinit var tvImageName: TextView
    private lateinit var tvSelectedFormat: TextView
    private lateinit var btnConvert: Button
    private lateinit var spinnerFormat: Spinner
    private lateinit var animationView: LottieAnimationView  // Lottie Animation view

    private var imagePath: String? = null
    private var imageName: String? = null
    private var isConversionComplete = false
    private var convertedFile: File? = null  // Track the converted file

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion)

        // Set up toolbar title with colored text
        val appTitle: TextView = findViewById(R.id.app_title)
        val spannable = SpannableString("Switch-IT")
        val redColor = ContextCompat.getColor(this, R.color.red)
        val yellowColor = ContextCompat.getColor(this, R.color.yellow)
        spannable.setSpan(ForegroundColorSpan(redColor), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(yellowColor), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(redColor), 8, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        appTitle.text = spannable

        // Initialize views
        tvImageName = findViewById(R.id.tv_image_name)
        tvSelectedFormat = findViewById(R.id.tv_selected_format)
        btnConvert = findViewById(R.id.btn_convert)
        spinnerFormat = findViewById(R.id.spinner_format)
        animationView = findViewById(R.id.splashAnimation) // Initialize Lottie animation view

        // Set up Spinner with a custom ArrayAdapter using spinner_item layout
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.conversion_formats,
            R.layout.spinner_item // Custom layout for Spinner item
        ).apply {
            setDropDownViewResource(R.layout.spinner_item) // Custom layout for dropdown item
        }
        spinnerFormat.adapter = adapter

        // Set default selection to PNG (index 0) and display it
        spinnerFormat.setSelection(0)
        updateSelectedFormatDisplay()

        // Update selected format display when a new item is chosen in the Spinner
        spinnerFormat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateSelectedFormatDisplay()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Receive image name and path from MainActivity
        imageName = intent.getStringExtra("imageName")
        imagePath = intent.getStringExtra("imagePath")

        // Display the image name or a fallback message
        tvImageName.text = imageName ?: getString(R.string.no_image_selected)

        // Handle conversion action
        btnConvert.setOnClickListener {
            if (isConversionComplete) {
                // Open the converted file once the conversion is complete
                convertedFile?.let {
                    openConvertedFile(it)
                }
            } else {
                val selectedFormat = spinnerFormat.selectedItem.toString()
                if (!imagePath.isNullOrEmpty()) {
                    // Start the Lottie animation when conversion starts
                    startConversionAnimation()
                    // Proceed with the image conversion with a delay for animation
                    Handler().postDelayed({
                        val file = convertImageToSelectedFormat(imagePath!!, selectedFormat)
                        if (file != null) {
                            convertedFile = file  // Store the converted file
                            isConversionComplete = true
                            btnConvert.text = "Open File" // Change button text after conversion
                        }
                    }, 2200)  // Delay for 2 seconds
                } else {
                    tvImageName.text = getString(R.string.error_no_image)
                }
            }
        }
    }

    private fun updateSelectedFormatDisplay() {
        val selectedFormat = spinnerFormat.selectedItem.toString()
        tvSelectedFormat.text = getString(R.string.spinner_format, selectedFormat)
    }

    private fun convertImageToSelectedFormat(path: String, format: String): File? {
        val convertedFile = when (format) {
            "PNG" -> convertImageToPNG(path)
            "JPEG" -> convertImageToJPEG(path)
            "PDF" -> convertImageToPDF(path)
            else -> null
        }

        // Notify user and save to gallery
        if (convertedFile != null) {
            tvImageName.text = getString(R.string.conversion_successful, convertedFile.name)
            saveFileToGallery(convertedFile)
        } else {
            tvImageName.text = getString(R.string.error_conversion_failed)
        }

        // Stop the animation after conversion
        stopConversionAnimation()
        return convertedFile
    }

    private fun convertImageToPNG(path: String): File? =
        convertImage(path, Bitmap.CompressFormat.PNG, "png")

    private fun convertImageToJPEG(path: String): File? =
        convertImage(path, Bitmap.CompressFormat.JPEG, "jpg")

    private fun convertImageToPDF(path: String): File? {
        val file = File(path)
        if (!file.exists()) return null

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        val outputFile = File(filesDir, "${file.nameWithoutExtension}.pdf")

        return try {
            FileOutputStream(outputFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            outputFile
        } catch (e: IOException) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun convertImage(path: String, format: Bitmap.CompressFormat, extension: String): File? {
        val file = File(path)
        if (!file.exists()) return null

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val outputFile = File(filesDir, "${file.nameWithoutExtension}.$extension")

        return try {
            FileOutputStream(outputFile).use { outputStream ->
                bitmap.compress(format, 100, outputStream)
            }
            outputFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveFileToGallery(convertedFile: File) {
        val contentResolver = contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, convertedFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, if (convertedFile.extension == "pdf") "application/pdf" else "image/*")
            put(MediaStore.MediaColumns.RELATIVE_PATH, if (convertedFile.extension == "pdf") "Documents/SwitchIT" else "Pictures/SwitchIT")
        }

        val collectionUri = if (convertedFile.extension == "pdf") {
            MediaStore.Files.getContentUri("external")
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val fileUri = contentResolver.insert(collectionUri, values)
        fileUri?.let {
            try {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    FileInputStream(convertedFile).copyTo(outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startConversionAnimation() {
        animationView.setAnimation("rocketman.json") // Set your Lottie file here
        animationView.repeatCount = LottieDrawable.INFINITE // Set repeat count to infinite
        animationView.setSpeed(1f) // Normal speed
        animationView.playAnimation()
    }

    private fun stopConversionAnimation() {
        animationView.cancelAnimation() // Stop the animation
    }

    private fun openConvertedFile(convertedFile: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            convertedFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }
}
