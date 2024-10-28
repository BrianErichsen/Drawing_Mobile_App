package com.example.Models

//import kotlinx.serialization.Serializable
//import kotlinx.serialization.descriptors.PrimitiveKind
//import org.jetbrains.exposed.dao.id.IntIdTable
//import java.sql.Blob
//
//
//@Serializable
//data class DrawingObject(
//    val id: Int,
//    val creatorId: String,
//    val title: String,
//    val modifiedDate: Long,
//    val createdDate: Long,
//    val filePath: String,
//    val thumbnail: String
//)
//
//object Drawing : IntIdTable() {
//    val creatorId = varchar("creatorId", 255)
//    val title = varchar("title", 255)
//    val modifiedDate = long("lastModifiedDate")
//    val createdDate = long("createdDate")
//    val filepath = varchar("filepath", 255)
//    val thumbnail = text("thumbnail")
//}