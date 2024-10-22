package com.example.drawing_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeListener(private val context: Context, private val onShake: (Float, Float) -> Unit) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var lastTime = 0L

    private val shakeThreshold = 3f // Sensitivity of shake detection

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime > 100) { // Check at intervals of 100ms
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // 计算速度并调用 onShake 回调
                val speed = sqrt((x - lastX) * (x - lastX) + (y - lastY) * (y - lastY) + (z - lastZ) * (z - lastZ)) / (currentTime - lastTime) * 10000

                if (speed > shakeThreshold) {
                    println("Shake detected with x: $x, y: $y") // 打印传感器的摇动值，便于调试
                    onShake(x, y) // 将 x 和 y 传递给回调
                }

                lastX = x
                lastY = y
                lastZ = z
                lastTime = currentTime
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this case
    }
}