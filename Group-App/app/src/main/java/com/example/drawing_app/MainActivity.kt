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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContent {
           val navController = rememberNavController()
           Surface {
               NavGraph(navController = navController, viewModel = drawingViewModel)
           }
       }
    }
}