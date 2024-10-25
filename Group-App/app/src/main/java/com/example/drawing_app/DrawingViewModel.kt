package com.example.drawing_app

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color  // 使用Compose的Color类
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

    private val _sensorPath = MutableLiveData<List<Point>>(emptyList())
    val sensorPath: LiveData<List<Point>> = _sensorPath
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

    // 用于存储小球的当前 X 和 Y 位置
    private val _currentX = MutableLiveData(0f)
    private val _currentY = MutableLiveData(0f)

    val currentX: LiveData<Float> = _currentX
    val currentY: LiveData<Float> = _currentY


    // 更新小球的位置信息
    fun updatePositionFromSensorData(x: Float, y: Float) {
        _currentX.value = x
        _currentY.value = y
    }

    // 更新传感器路径信息
    fun updatePathFromSensorData(sensorData: FloatArray, previousX: Float, previousY: Float, canvasWidth: Float, canvasHeight: Float) {
        // 通过传感器数据计算新的 x 和 y 位置
        val sensorX = sensorData[0]  // 获取 X 轴的传感器数据
        val sensorY = sensorData[1]  // 获取 Y 轴的传感器数据

        // 更新小球的位置
        val newX = (previousX + sensorX * 10).coerceIn(0f, canvasWidth - 20f)  // 限制在画布内
        val newY = (previousY + sensorY * 10).coerceIn(0f, canvasHeight - 20f)

        // 更新 ViewModel 中的 currentX 和 currentY
        updatePositionFromSensorData(newX, newY)

        // 创建新的路径点，并确保路径点位于画布边界内
        val newPoint = Point(newX, newY, Color.Red, 10f)

        // 更新路径
        val updatedPath = _sensorPath.value.orEmpty() + newPoint
        _sensorPath.postValue(updatedPath)
    }
}




// end of ViewModel Implementation

