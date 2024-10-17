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

    // pencil tool state data
    val pencil = remember { Pencil() }
    var showPencilOptions by remember { mutableStateOf(false) }

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
        Button(
            onClick = { showPencilOptions = !showPencilOptions },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Toggle Pencil Options")
        }
        Button(
            onClick = { if(filePath != null) {
                shareDrawing(context, filePath!!)
            } else {
                Toast.makeText(context, "No drawing available to share", Toast.LENGTH_SHORT).show()
            }
    }) {
            Text("Share Drawing")
        }

        if (showPencilOptions) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row {
                    Button(
                        onClick = { pencil.changePencilColor(Color.Black) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.size(40.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { pencil.changePencilColor(Color.Red) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.size(40.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { pencil.changePencilColor(Color.Blue) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.size(40.dp)
                    ) {}
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Size Slider
                Text("Pencil Size: ${pencil.size.value.toInt()}")
                Slider(
                    value = pencil.size.value,
                    onValueChange = { pencil.changePencilSize(it) },
                    valueRange = 5f..50f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Eraser Toggle
                Button(
                    onClick = { pencil.toggleEraser() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (pencil.isErasing.value) "Stop Erasing" else "Eraser")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } // end of if show pen options UI elements
        // Drawing area
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath.add(Point(offset.x, offset.y, pencil.color.value, pencil.size.value))
                        },
                        onDrag = { change, _ ->
                            val point = change.position
                            currentPath.add(Point(point.x, point.y, pencil.color.value, pencil.size.value))
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
    } // end of column scope
}
