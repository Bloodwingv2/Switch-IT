package com.example.switch_it

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ConversionActivity : AppCompatActivity() {

    private lateinit var tvImageName: TextView
    private lateinit var btnConvert: Button
    private lateinit var spinnerFormat: Spinner

    private var imagePath: String? = null
    private var imageName: String? = null

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
        btnConvert = findViewById(R.id.btn_convert)
        spinnerFormat = findViewById(R.id.spinner_format)

        // Receive image name and path from MainActivity
        imageName = intent.getStringExtra("imageName")
        imagePath = intent.getStringExtra("imagePath")

        // Display the image name
        tvImageName.text = imageName ?: "No Image Selected"

        // Handle conversion action
        btnConvert.setOnClickListener {
            val selectedFormat = spinnerFormat.selectedItem.toString()
            if (!imagePath.isNullOrEmpty()) {
                convertImageToSelectedFormat(imagePath!!, selectedFormat)
            }
        }
    }

    /**
     * Converts the image to the selected format and saves it.
     * @param path The path to the image
     * @param format The format to convert the image to
     */
    private fun convertImageToSelectedFormat(path: String, format: String) {
        val convertedFile = when (format) {
            "PNG" -> convertImageToPNG(path)
            "JPEG" -> convertImageToJPEG(path)
            "PDF" -> convertImageToPDF(path)
            else -> null
        }

        // Notify user and save to gallery
        if (convertedFile != null) {
            tvImageName.text = "Conversion successful: ${convertedFile.name}"
            saveFileToGallery(convertedFile)
        } else {
            tvImageName.text = "Error in conversion"
        }
    }

    private fun convertImageToPNG(path: String): File? = convertImage(path, Bitmap.CompressFormat.PNG, "png")
    private fun convertImageToJPEG(path: String): File? = convertImage(path, Bitmap.CompressFormat.JPEG, "jpg")

    /**
     * Generalized image conversion method
     */
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

    /**
     * Saves the converted file to the appropriate directory using MediaStore.
     * @param convertedFile The converted file (image or PDF)
     */
    private fun saveFileToGallery(convertedFile: File) {
        val contentResolver = contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, convertedFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, if (convertedFile.extension == "pdf") "application/pdf" else "image/*")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                if (convertedFile.extension == "pdf") "Documents/SwitchIT" else "Pictures/SwitchIT"
            )
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
}
