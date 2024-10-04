package com.example.drawing_app
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.Button

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp

@Composable
fun drawingListScreen(viewModel: DrawingViewModel, onNavigateToDraw: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.drawingList.value ?: listOf()) { drawing ->
                Image(
                    bitmap = drawing.asImageBitmap(),
                    contentDescription = "Drawing",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
        // Button to navigate to the drawing screen
        Button(
            onClick = onNavigateToDraw,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Create New Drawing")
        }
    }
}
