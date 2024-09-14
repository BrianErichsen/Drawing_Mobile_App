package com.example.drawing_app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private var currentX = 0f
    private var currentY = 0f

    private var isEraserEnabled = false

    // enum for different shapes
    enum class PenShape {
        CIRCLE, SQUARE, TRIANGLE, OVAL
    }

    private var currentPenShape = PenShape.CIRCLE

    init {
        setBackgroundColor(Color.LTGRAY)
        post {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }
            canvas = Canvas(bitmap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentX = event.x
        currentY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                addDrawing(currentX, currentY)
            }
        }
        return true
    }

    fun addDrawing(x: Float, y: Float) {
        when (currentPenShape) {
            PenShape.CIRCLE -> canvas?.drawCircle(x, y, paint.strokeWidth, paint)
            PenShape.SQUARE -> {
                val halfSize = paint.strokeWidth / 2
                canvas?.drawRect(x - halfSize, y - halfSize, x + halfSize, y + halfSize, paint)
            }
            PenShape.TRIANGLE -> {
                val path = Path()
                val halfSize = paint.strokeWidth
                path.moveTo(x, y - halfSize)
                path.lineTo(x - halfSize, y + halfSize)
                path.lineTo(x + halfSize, y + halfSize)
                path.close()
                canvas?.drawPath(path, paint)
            }
            PenShape.OVAL -> canvas?.drawOval(
                x - paint.strokeWidth,
                y - paint.strokeWidth / 2,
                x + paint.strokeWidth,
                y + paint.strokeWidth / 2,
                paint
            )
        }
        invalidate()
    }

    fun getBitMap(): Bitmap? {
        return bitmap?.copy(Bitmap.Config.ARGB_8888, true)
    }

    fun setBitMap(savedBitmap: Bitmap) {
        bitmap = savedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(bitmap!!)
        invalidate()
    }

    fun setPenColor(color: Int) {
        paint.color = color
        isEraserEnabled = false
    }

    fun setPenSize(size: Float) {
        paint.strokeWidth = size
    }

    fun setPenShape(shape: PenShape) {
        currentPenShape = shape
    }

    fun enableEraser() {
        paint.color = Color.LTGRAY //background color
        isEraserEnabled = true
        paint.strokeWidth = 20f
    }

    fun observeViewModel(viewModel: DrawingViewModel, lifecycleOwner: LifecycleOwner) {
        viewModel.penColor.observe(lifecycleOwner, Observer { color ->
            setPenColor(color)
        })
        viewModel.penSize.observe(lifecycleOwner, Observer { size ->
            setPenSize(size)
        })
        viewModel.penShape.observe(lifecycleOwner, Observer { shape ->
            setPenShape(shape)
        })
        viewModel.bitmap.observe(lifecycleOwner, Observer { bitmap ->
            bitmap?.let {
                setBitMap(it)
            }
        })
    }//end of observeVM method
}
