package com.example.drawing_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.compose.ui.graphics.Path
//import android.graphics.Path
import android.widget.Toast
import androidx.compose.foundation.Canvas
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.withContext




data class Point(val x: Float, val y: Float, val color: Color, val size: Float)

@Composable
fun DrawingCanvas(navController: NavController, drawingId: Int?, viewModel: DrawingViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var currentPath = remember { mutableStateListOf<Point>() }
    var savedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) } // For displaying the loaded image
    var filePath by remember { mutableStateOf<String?>(null) }
    val creatingNewDrawing = drawingId == -1

    // State to control if we are currently drawing

    // Load existing drawing if `drawingId` is provided
    LaunchedEffect(drawingId) {
        if (drawingId != null && drawingId != -1) {
            scope.launch(Dispatchers.IO) {
                try {
                    val drawing = viewModel.getDrawingById(drawingId)
                    drawing?.let {
                        name = it.name
                        filePath = it.filePath
                        val file = File(it.filePath)
                        if (file.exists()) {
                            val loadedBitmap = BitmapFactory.decodeFile(file.path)
                            savedImageBitmap = loadedBitmap.asImageBitmap() // Convert to ImageBitmap for display
                        }
                    }
                } catch (e: Exception) {
                    println("Error loading image: ${e.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Drawing area
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath.add(Point(offset.x, offset.y, Color.Black, 5f))
                        },
                        onDrag = { change, _ ->
                            val point = change.position
                            currentPath.add(Point(point.x, point.y, Color.Black, 5f))
                        }
                    )
                }
        ) {
            // Draw the saved image if available
            savedImageBitmap?.let { image ->
                drawImage(image = image, topLeft = Offset.Zero)
            }

            // Draw current paths
            for (point in currentPath) {
                drawCircle(
                    color = point.color,
                    radius = point.size,
                    center = Offset(point.x, point.y)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (creatingNewDrawing) {
            BasicTextField(
                value = name,
                onValueChange = {name = it},
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerText ->
                    Box(
                        Modifier
                            .background(Color.Yellow)
                            .padding(16.dp)
                    ) {
                        if (name.isEmpty()) {
                            Text("Please enter a name for your drawing")
                            innerText()
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Save button
        Button(
            onClick = {
                val bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
                val canvas = AndroidCanvas(bitmap)
                savedImageBitmap?.let { imageBitmap ->
                    canvas.drawBitmap(imageBitmap.asAndroidBitmap(), 0f, 0f, null)
                }
                for (point in currentPath) {
                    canvas.drawCircle(
                        point.x,
                        point.y,
                        point.size,
                        Paint().apply {
                            color = point.color.toArgb()
                            style = Paint.Style.FILL
                        }
                    )
                }

                scope.launch(Dispatchers.IO) {
                    if (creatingNewDrawing) {
                        filePath = File(context.filesDir, "$name.png").path
                    }
                    if (filePath != null) {
                        saveDrawing(
                            context = context,
                            bitmap = bitmap,
                            name = name.ifEmpty { "Untitled Drawing" },
                            filePath = filePath!!,
                            viewModel = viewModel,
                            navController = navController,
                            drawingId = drawingId
                        )
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: No file path!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Drawing")
        }
    }
}
