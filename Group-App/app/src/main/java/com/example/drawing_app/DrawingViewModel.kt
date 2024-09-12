package com.example.drawing_app
// DrawingViewModel.kt


import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawingViewModel : ViewModel() {
    private val _bitmap = MutableLiveData<Bitmap?>()
    val bitmap: LiveData<Bitmap?> get() = _bitmap

    private val _penColor = MutableLiveData(Color.BLACK)
    val penColor: LiveData<Int> get() = _penColor

    private val _penSize = MutableLiveData(5f)
    val penSize: LiveData<Float> get() = _penSize

    private val _penShape = MutableLiveData(DrawingView.PenShape.CIRCLE)
    val penShape: LiveData<DrawingView.PenShape> get() = _penShape

    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun setPenColor(color: Int) {
        _penColor.value = color
    }

    fun setPenSize(size: Float) {
        _penSize.value = size
    }

    fun setPenShape(shape: DrawingView.PenShape) {
        _penShape.value = shape
    }

    fun clearBitmap() {
        _bitmap.value = null
    }
}
