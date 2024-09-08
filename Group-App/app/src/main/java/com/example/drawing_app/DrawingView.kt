package com.example.drawing_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    enum class PenShape {
        CIRCLE, SQUARE //still will add the other shapes
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

    fun addDrawing(x: Float, y: Float) {
        when (currentPenShape) {
            PenShape.CIRCLE -> canvas?.drawCircle(x, y, paint.strokeWidth, paint)
            PenShape.SQUARE -> {
                val halfSize = paint.strokeWidth / 2
                canvas?.drawRect(x - halfSize, y - halfSize, x + halfSize, y + halfSize, paint)
            }
        }
        //canvas?.drawCircle(x, y, 20f, paint)
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
    }
    fun setPenSize(size: Float) {
        paint.strokeWidth = size
    }

    fun setPenShape(shape: PenShape) {
        currentPenShape = shape
    }
}//end of DrawingView class implementation