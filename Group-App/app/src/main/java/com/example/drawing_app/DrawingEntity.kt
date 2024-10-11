package com.example.drawing_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val  name: String,
    val filePath: String
)

