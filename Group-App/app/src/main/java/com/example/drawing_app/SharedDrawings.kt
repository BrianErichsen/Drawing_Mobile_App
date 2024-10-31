package com.example.drawing_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.drawing_app.network.ApiViewModel
import com.example.drawing_app.network.DrawingResponse
import com.google.firebase.auth.FirebaseAuth
import java.net.URL

import io.ktor.utils.io.concurrent.shared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SharedDrawingsScreen(apiViewModel: ApiViewModel) {
    val sharedDrawings by apiViewModel.sharedImages.observeAsState(emptyList())
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        Log.d("SharedDrawingsScreen", "Fetching shared drawings")
        userId?.let {
            apiViewModel.fetchSharedImages(it)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Shared Drawings", style = MaterialTheme.typography.titleLarge)
        if (sharedDrawings.isEmpty()) {
            Text("No shared drawings available.")
        } else
            LazyColumn {
                items(sharedDrawings) { drawing ->
                    DrawingItem(drawing, apiViewModel)
                }
            }
        }
}

@Composable
fun DrawingItem(drawing: DrawingResponse, apiViewModel: ApiViewModel) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(drawing.imageUrl) {
        val bitmap = loadImageFromUrl(drawing.imageUrl)
        imageBitmap = bitmap
    }

    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Drawing ID: ${drawing.id}")
            Text("User ID: ${drawing.userId}")
            Text("Shared: ${drawing.shared}")

            // displays the image
            imageBitmap?.let {
                Image(bitmap = it, contentDescription = "Shared Drawing", modifier = Modifier.size(200.dp).padding(top = 8.dp))
            }
        }
    }
}

suspend fun loadImageFromUrl(url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = URL(url).openStream()
            val byteArray = inputStream.readBytes()

            // Bitmap options to set preferred config
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            // Decode with specified options
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}