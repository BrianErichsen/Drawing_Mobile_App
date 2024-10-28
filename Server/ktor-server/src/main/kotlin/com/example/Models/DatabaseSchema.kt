package com.example.Models

import com.example.Models.Drawings.autoIncrement
import com.example.Models.Drawings.default
import com.example.Models.Users.uniqueIndex
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = varchar("id", 50)// Firebase UID or email as identifier
    val email = varchar("email", 100).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object Drawings : IntIdTable() {
    val userId = varchar("user_id", 50).references(Users.id)
    val imageUrl = varchar("image_url", 255) // Path or URL to the image
    val shared = bool("shared").default(false) // Determines if it's publicly shared
}