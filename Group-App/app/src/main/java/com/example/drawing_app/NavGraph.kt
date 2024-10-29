package com.example.drawing_app

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavController,
    viewModel: DrawingViewModel,
    onShakeCallback: (() -> Unit) -> Unit,
    onSensorEnabledChanged: (Boolean) -> Unit
) {
    // 初始化 FirebaseAuth 实例
    val auth = FirebaseAuth.getInstance()

    NavHost(navController = navController as NavHostController, startDestination = "login_page") {
        composable("login_page") {
            // 在登录页面调用 LoginScreen，并传递导航成功后的回调
            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable("main_page") {
            // 主页面：显示“创建新绘图”或“编辑绘图”界面
            MainPage(navController = navController, viewModel = viewModel)
        }
        composable("create_drawing") {
            DrawingCanvas(
                navController = navController,
                drawingId = -1,
                viewModel = viewModel,
                onShakeCallback = onShakeCallback,
                onSensorEnabledChanged = onSensorEnabledChanged
            )
        }
        composable("edit_drawing/{drawingId}") { navBackStackEntry ->
            val drawingId = navBackStackEntry.arguments?.getString("drawingId")?.toInt() ?: -1
            DrawingCanvas(
                navController = navController,
                drawingId = drawingId,
                viewModel = viewModel,
                onShakeCallback = onShakeCallback,
                onSensorEnabledChanged = onSensorEnabledChanged
            )
        }
    }
}
