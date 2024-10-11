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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DrawingViewModel(private val repository: DrawingRepository) : ViewModel() {

    val drawingList: Flow<List<DrawingEntity>> = repository.allDrawings

    fun insertDrawing(drawing: DrawingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(drawing)
        }
    }

    fun updateDrawing(drawing: DrawingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(drawing)
        }
    }

    suspend fun getDrawingById(id: Int): DrawingEntity? {
        return repository.getDrawing(id)
    }

} // end of ViewModel Implementation
