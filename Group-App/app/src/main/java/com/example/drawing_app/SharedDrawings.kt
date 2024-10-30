package com.example.drawing_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drawing_app.network.ApiViewModel
import com.example.drawing_app.network.DrawingResponse

@Composable
fun SharedDrawingsScreen(apiViewModel: ApiViewModel) {
    val sharedDrawings by apiViewModel.sharedImages.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        apiViewModel.fetchSharedImages()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Shared Drawings", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(sharedDrawings) { drawing ->
                DrawingItem(drawing = drawing, apiViewModel = apiViewModel)
            }
        }
    }
}

@Composable
fun DrawingItem(drawing: DrawingResponse, apiViewModel: ApiViewModel) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Drawing ID: ${drawing.id}")
            Text("User ID: ${drawing.userId}")
            Text("Shared: ${drawing.shared}")

            Row {
                Button(onClick = { apiViewModel.shareImage(drawing.id) }) {
                    Text("Share")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { apiViewModel.unshareImage(drawing.id) }) {
                    Text("Unshare")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { apiViewModel.deleteImage(drawing.id) }) {
                    Text("Delete")
                }
            }
        }
    }
}
