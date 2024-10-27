package com.example.drawing_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController

class MainActivity : AppCompatActivity() {
    private val drawingViewModel: DrawingViewModel by viewModels {
        DrawingViewModelFactory((application as DrawingApplication).repository)
    }
    private lateinit var shakeListener: ShakeListener
    private var onShakeCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shakeListener = ShakeListener(this) {
            onShakeDetected()
        }
       setContent {
           val navController = rememberNavController()
           Surface {
               NavGraph(navController = navController, viewModel = drawingViewModel, onShakeCallback = {
                   callback -> onShakeCallback = callback
               }, onSensorEnabledChanged = { enabled ->
                   toggleShakeListener(enabled)
               })
           }
       }
    }

    override fun onResume() {
        super.onResume()
        shakeListener.start()
    }
    override fun onPause() {
        super.onPause()
        shakeListener.stop()
    }

    private fun onShakeDetected() {
        onShakeCallback?.invoke()
    }

    private fun toggleShakeListener(sensorEnabled: Boolean) {
        if (sensorEnabled) {
            shakeListener.stop()
        } else {
            shakeListener.start()
        }
    }
}// end of MainActivity implementation