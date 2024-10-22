package com.example.drawing_app

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(
    navController: NavController,
    viewModel: DrawingViewModel,
    onShakeCallback: () -> Unit, // 修改为无参数的回调
    onGravitySensorUpdate: (Float, Float) -> Unit // 新增用于重力传感器数据的回调
) {
    NavHost(navController = navController as NavHostController, startDestination = "login_page") {
        composable("login_page") {
            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable("create_drawing") {
            DrawingCanvas(
                navController = navController,
                drawingId = -1,
                viewModel = viewModel,
                onGravitySensorUpdate = onGravitySensorUpdate // 传递重力传感器回调
            )
        }
        composable("edit_drawing/{drawingId}") { navBackStackEntry ->
            val drawingId = navBackStackEntry.arguments?.getString("drawingId")?.toInt() ?: -1
            DrawingCanvas(
                navController = navController,
                drawingId = drawingId,
                viewModel = viewModel,

                onGravitySensorUpdate = onGravitySensorUpdate // 传递重力传感器回调
            )
        }
    }
}
