package com.example.drawing_app

import kotlinx.coroutines.flow.Flow

class DrawingRepository(private val drawingDao: DrawingDao) {
    val allDrawings: Flow<List<DrawingEntity>> = drawingDao.getAllDrawings()

    suspend fun insert(drawing: DrawingEntity) {
        drawingDao.insertDrawing(drawing)
    }
    suspend fun getDrawing(id: Int): DrawingEntity? {
        return drawingDao.getDrawingById(id)
    }

    suspend fun update(drawing: DrawingEntity) {
        drawingDao.update(drawing)
    }
}