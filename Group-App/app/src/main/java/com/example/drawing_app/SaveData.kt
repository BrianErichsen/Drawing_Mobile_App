package com.example.drawing_app

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

import android.widget.Toast
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import android.content.Intent
import androidx.core.content.FileProvider
import android.net.Uri

suspend fun saveDrawing(
    context: Context,
    bitmap: Bitmap?,
    name: String?,
    filePath: String?,
    viewModel: DrawingViewModel,
    navController: NavController,
    drawingId: Int?
) {
    try {
        if (filePath == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: No file path provided", Toast.LENGTH_SHORT).show()
            }
            return
        }

        if (bitmap == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: Bitmap is empty", Toast.LENGTH_SHORT).show()
            }
            return
        }

        if (name.isNullOrEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: No drawing name", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        if (drawingId == null || drawingId == -1) {
            viewModel.insertDrawing(DrawingEntity(name = name, filePath = filePath))
        } else {
            viewModel.updateDrawing(DrawingEntity(id = drawingId, name = name, filePath = filePath))
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Drawing saved successfully", Toast.LENGTH_SHORT).show()
            navController.navigate("login_page")
        }
    } catch (e: IOException) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: Failed to save drawing ${e.message}", Toast.LENGTH_SHORT).show()
        }
        e.printStackTrace()
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        e.printStackTrace()
    }
} // end of saveDrawing co-routine method implementation

fun shareDrawing(context: Context, filePath: String) {
    val file = File(filePath)

    if (file.exists()) {
        // Use a FileProvider to grant access to the image file
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Must match the authority in AndroidManifest
            file
        )

        // Create the sharing intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Launch the sharing dialog
        context.startActivity(Intent.createChooser(shareIntent, "Share drawing via"))
    } else {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
    }
}