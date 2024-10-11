package com.example.drawing_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.Text

@Composable
fun LoginScreen(navController: NavController, viewModel:DrawingViewModel) {
    val drawings by viewModel.drawingList.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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