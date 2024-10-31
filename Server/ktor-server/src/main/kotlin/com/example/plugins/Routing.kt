package com.example.plugins

import com.example.DBSettings
import com.example.Models.Drawings
import com.example.Models.Users
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.InputStream

@Serializable
data class ImageResponse(val id: Int, val userId: String , val imageUrl: String, val shared: Boolean)

fun Application.configureDatabaseDefaults() {
    transaction {
        if (Users.select { Users.id eq "guest_user" }.empty()) {
            Users.insert {
                it[id] = "guest_user"
                it[email] = "guest@example.com" // Placeholder email for guest user
            }
        }
    }
}

fun Application.configureRouting() {
    install(Resources)

    // Set up a directory for storing uploaded images if not using a URL path
    val uploadDir = File("uploads")
    if (!uploadDir.exists()) {
        uploadDir.mkdir()
    }

    routing {
        static("/uploads") {
            files("uploads")
        }

        // Health check
        get("/") {
            call.respondText("Hello World!")
        }

        // Upload Image Endpoint with Multipart Form Handling
        post("/images/upload") {
            val multipart = call.receiveMultipart()
            var userId: String? = null
            var imageUrl: String? = null
            var imageId: Int? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "userId") {
                            userId = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "file") {
                            val finalUserId = userId ?: "guest_user"
                            transaction {
                                imageId = Drawings.insertAndGetId {
                                    it[Drawings.userId] = finalUserId
                                    it[Drawings.imageUrl] = "" // Temporary placeholder
                                    it[Drawings.shared] = true
                                }.value
                            }

                            val fileName = "${imageId}.png"
                            val file = File(uploadDir, fileName)
                            file.outputStream().buffered().use { output ->
                                (part.provider() as? InputStream)?.use { input ->
                                    input.copyTo(output)
                                }
                            }

                            imageUrl = "/uploads/$fileName"
                            transaction {
                                Drawings.update( { Drawings.id eq imageId!! }) {
                                    it[Drawings.imageUrl] = imageUrl!!
                                }
                            }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            // Ensure userId and imageUrl are not null
            if (userId == null || imageUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing userId or file")
                return@post
            }


//
//                // Insert image with user ID as a direct reference
//                imageId = Drawings.insertAndGetId {
//                    it[Drawings.userId] = userId!!
//                    it[Drawings.imageUrl] = imageUrl!!
//                    it[Drawings.shared] = true
//                }.value
//            }

            // Ensure userId and imageUrl are not null
            if (userId == null || imageUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing userId or file")
            } else {
                call.respond(HttpStatusCode.Created, "Image uploaded with ID: $imageId")
            }
            //call.respond(HttpStatusCode.Created, "Image uploaded with ID: $imageId")
        }

        // Fetch Shared Images Endpoint
        get("/images/shared") {
            val baseUrl = call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port()
            val sharedImages = transaction {
                Drawings.join(Users, JoinType.INNER, additionalConstraint = { Drawings.userId eq Users.id})
                    .select { Drawings.shared eq true }
                    .map {
                        ImageResponse(
                            id = it[Drawings.id].value,
                            userId = it[Users.id],
                            imageUrl = baseUrl + it[Drawings.imageUrl],
                            shared = it[Drawings.shared]
                        )
                    }
            }
            call.respond(sharedImages)
        }

        // Share Image Endpoint
        post("/images/{id}/share") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                transaction {
                    Drawings.update({ Drawings.id eq id }) {
                        it[Drawings.shared] = true
                    }
                }
                call.respond(HttpStatusCode.OK, "Image with ID: $id shared successfully.")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid image ID.")
            }
        }

        // Unshare Image Endpoint
        post("/images/{id}/unshare") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                transaction {
                    Drawings.update({ Drawings.id eq id }) {
                        it[Drawings.shared] = false
                    }
                }
                call.respond(HttpStatusCode.OK, "Image with ID: $id unshared successfully.")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid image ID.")
            }
        }

        // Delete Image Endpoint
        delete("/images/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                transaction {
                    Drawings.deleteWhere { Drawings.id eq id }
                }
                call.respond(HttpStatusCode.OK, "Image with ID: $id deleted successfully.")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid image ID.")
            }
        }
    }
} //