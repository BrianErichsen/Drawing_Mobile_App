package com.example.drawing_app

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawingViewModel : ViewModel() {

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
}
