package com.example.drawing_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.drawing_app.network.ApiRepository
import com.example.drawing_app.network.ApiService
import com.example.drawing_app.network.ApiViewModel
import com.example.drawing_app.network.ApiViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {
    private val drawingViewModel: DrawingViewModel by viewModels {
        DrawingViewModelFactory((application as DrawingApplication).repository)
    }
    private val apiViewModel: ApiViewModel by viewModels {
        ApiViewModelFactory(ApiRepository(CoroutineScope(Dispatchers.IO), ApiService()))
    }
    private lateinit var shakeListener: ShakeListener
    private var onShakeCallback: (() -> Unit)? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        shakeListener = ShakeListener(this) {
            onShakeDetected()
        }
       setContent {
           val navController = rememberNavController()
           Surface {
               NavGraph(navController = navController, viewModel = drawingViewModel, apiViewModel = apiViewModel,
                   onShakeCallback = {
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