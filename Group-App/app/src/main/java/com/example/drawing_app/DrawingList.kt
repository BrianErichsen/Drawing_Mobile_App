package com.example.drawing_app
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap

@Composable
fun drawingList(drawings: List<Bitmap>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(drawings) { drawing ->
            Image(
                bitmap = drawing.asImageBitmap(),
                contentDescription = "Drawing",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
