package com.example.drawing_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DrawingDao {
    @Insert
    suspend fun insertDrawing(drawing: DrawingEntity)

    @Query("SELECT * FROM drawings")
    suspend fun getAllDrawings(): List<DrawingEntity>

    @Query("DELETE FROM drawings WHERE id = :id")
    suspend fun deleteDrawingById(id: Long)
}
