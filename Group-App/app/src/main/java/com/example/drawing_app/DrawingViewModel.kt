package com.example.drawing_app

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.BitmapFactory

class DrawingViewModel(application: Application) : AndroidViewModel(application) {
    // Dao for database access
    private val drawingDao: DrawingDao = DrawingDatabase.getDatabase(application).drawingDao()

    // Holds the current bitmap for drawing
    private val _bitmap = MutableLiveData<Bitmap?>()
    val bitmap: LiveData<Bitmap?> get() = _bitmap

    // Holds the list of all drawings (multiple bitmaps)
    private val _drawingList = MutableLiveData<List<Bitmap>>(emptyList())
    val drawingList: LiveData<List<Bitmap>> get() = _drawingList

    // Pen properties
    private val _penColor = MutableLiveData(Color.BLACK)
    val penColor: LiveData<Int> get() = _penColor

    private val _penSize = MutableLiveData(5f)
    val penSize: LiveData<Float> get() = _penSize

    private val _penShape = MutableLiveData(DrawingView.PenShape.CIRCLE)
    val penShape: LiveData<DrawingView.PenShape> get() = _penShape

    // Sets the current bitmap (for ongoing drawing)
    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    // Adds the current drawing to the drawing list
    fun addDrawingToList() {
        _bitmap.value?.let { currentBitmap ->
            val currentList = _drawingList.value?.toMutableList() ?: mutableListOf()
            currentList.add(currentBitmap)
            _drawingList.value = currentList
            clearBitmap()  // Clears the current bitmap after adding it to the list
        }
    }

    // Clears the current bitmap, made private as it's only used internally
    private fun clearBitmap() {
        _bitmap.value = null
    }

    // Pen customization functions
    fun setPenColor(color: Int) {
        _penColor.value = color
    }

    fun setPenSize(size: Float) {
        _penSize.value = size
    }

    fun setPenShape(shape: DrawingView.PenShape) {
        _penShape.value = shape
    }

    // Clears the entire drawing list
    fun clearDrawingList() {
        _drawingList.value = emptyList()
    }

    fun saveDrawing(bitmap: Bitmap, fileName: String?) {
        viewModelScope.launch {
            val genFilename = fileName ?: "drawing_${System.currentTimeMillis()}.png"
            val filePath = saveBitmapToFile(getApplication(), bitmap, genFilename) // saves bitmap to file
            val drawing = DrawingEntity(filePath = filePath) // creates entity
            drawingDao.insertDrawing(drawing) // inserts into the database
        }
    }

    fun loadDrawings() {
        viewModelScope.launch {
            val drawings = drawingDao.getAllDrawings()
            _drawingList.postValue(drawings.mapNotNull {
                loadBitmapFromFile(it.filePath) })
        }
    }
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String): String {
        //val filename = "drawing_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, filename)

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            // Compress bitmap to PNG and save it to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream?.flush()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Return the file path of the saved bitmap
        return file.absolutePath
    }
    fun loadBitmapFromFile(filePath: String): Bitmap? {
        val file = File(filePath)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
        //return BitmapFactory.decodeFile(file.absolutePath)
    }
}
