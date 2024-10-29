package com.example.drawing_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment


@Composable
fun MainPage(navController: NavController, viewModel: DrawingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to the Main Page!")
        Button(onClick = {
            navController.navigate("create_drawing")
        }) {
            Text("Create New Drawing")
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: DrawingViewModel) {
    // Firebase Authentication
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val drawings by viewModel.drawingList.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Email and Password Fields
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Register and Login Buttons
        Button(
            onClick = { registerUser(auth, email, password) { message = "Registration successful" } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { loginUser(auth, email, password, navController) { message = "Login successful" } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Text(message, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Existing UI for drawing list
        Button(
            onClick = {
                navController.navigate("create_drawing")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Drawing")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (drawings.isNotEmpty()) {
            Text("Edit Drawing", modifier = Modifier.padding(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(drawings) { drawing ->
                    Button(
                        onClick = {
                            navController.navigate("edit_drawing/${drawing.id}")
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        Text(drawing.name)
                    }
                }
            }
        } else {
            Text("Sorry but no drawings available", modifier = Modifier.padding(7.dp))
        }
    }
}

// Helper functions for registering and logging in users
private fun registerUser(auth: FirebaseAuth, email: String, password: String, onSuccess: () -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                // Handle registration failure, for example, by displaying a message
            }
        }
}

private fun loginUser(auth: FirebaseAuth, email: String, password: String, navController: NavController, onSuccess: () -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess() // 调用 onSuccess 回调来更新消息或其他处理
                navController.navigate("main_page") // 登录成功后导航到主界面
            } else  {
                // Handle login failure, for example, by displaying a message
            }
        }
}
