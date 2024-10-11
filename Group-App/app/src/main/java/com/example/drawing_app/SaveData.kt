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
}