package com.example.drawing_app

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf

class Pencil {
    var color = mutableStateOf(Color.Black)
    var size = mutableFloatStateOf(10f)
    //var isLineDrawing = mutableStateOf(false)
    var isErasing = mutableStateOf(false)

    fun changePencilColor(newColor : Color) {
        color.value = newColor
        isErasing.value = false
    }
    fun changePencilSize(newSize : Float) {
        size.value = newSize
    }
    fun toggleEraser() {
        isErasing.value = !isErasing.value
        if (isErasing.value) {
            color.value = Color.White
        }
    }
} // end of Pencil class implementation