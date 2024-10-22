package com.example.drawing_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val drawingViewModel: DrawingViewModel by viewModels {
        DrawingViewModelFactory((application as DrawingApplication).repository)
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var gravitySensor: Sensor
    var onGravitySensorUpdate: ((Float, Float) -> Unit)? = null
    var onShakeCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取系统服务以访问传感器
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.let { sensor ->
            gravitySensor = sensor
        }

// 使用重力传感器

        setContent {
            val navController = rememberNavController()
            Surface {
                NavGraph(
                    navController = navController,
                    viewModel = drawingViewModel,
                    onGravitySensorUpdate = { x: Float, y: Float -> onGravitySensorChanged(x, y) },  // 传递重力传感器数据
                    onShakeCallback = { onShakeDetected() }  // 传递摇动事件的回调，用于增加笔大小
                )
            }
        }
    }

    // 传感器数据变化时的回调
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
            val x = event.values[0] // 重力传感器的 x 轴值
            val y = event.values[1] // 重力传感器的 y 轴值

            // 调用回调函数，将 x 和 y 传递给 UI 以更新球的位置
            onGravitySensorUpdate?.invoke(x, y)
        }

        // 这里你可以添加处理摇动传感器的代码，如果需要可以根据阈值触发 onShakeCallback。
    }

    // 当传感器的精度发生变化时调用 (此处不需要实现)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    // 当重力传感器值变化时更新 UI 中球的位置
    private fun onGravitySensorChanged(x: Float, y: Float) {
        // 这里是对接的回调，UI 中会根据传感器数据更新球的位置
        println("Gravity sensor changed with x: $x, y: $y")
    }

    // 摇动事件回调，增加笔大小
    private fun onShakeDetected() {
        println("Shake detected! Increasing pen size.")
        onShakeCallback?.invoke()
    }

    // 在 Activity 重新可见时，注册传感器监听
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI)
    }

    // 在 Activity 不可见时，取消传感器监听
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
