package com.example.drawing_app

import android.app.Application


class DrawingApplication : Application() {

    val database by lazy { DrawingDatabase.getDatabase(this) }
    val repository by lazy { DrawingRepository(database.drawingDao()) }
}