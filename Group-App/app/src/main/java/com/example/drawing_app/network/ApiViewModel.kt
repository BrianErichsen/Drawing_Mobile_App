package com.example.drawing_app.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    val sharedImages: LiveData<List<DrawingResponse>> = repository.sharedImages

    fun fetchSharedImages() = repository.fetchSharedImages()

    fun uploadImage(userId: String, imageUrl: String) = repository.uploadImage(userId, imageUrl)

    fun shareImage(imageId: Int) = repository.shareImage(imageId)

    fun unshareImage(imageId: Int) = repository.unshareImage(imageId)

    fun deleteImage(imageId: Int) = repository.deleteImage(imageId)
}
