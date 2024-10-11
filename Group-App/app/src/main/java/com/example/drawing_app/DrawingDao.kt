package com.example.drawing_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrawing(drawing: DrawingEntity)



    @Query("DELETE FROM drawings WHERE id = :id")
    fun deleteDrawingById(id: Long)

    @Update
    fun update(drawing: DrawingEntity)

    @Query("SELECT * FROM drawings WHERE id = :id")
    fun getDrawingById(id: Int): DrawingEntity?

    @Query("SELECT * FROM drawings ORDER BY id DESC")
    fun getAllDrawings(): Flow<List<DrawingEntity>>
}
