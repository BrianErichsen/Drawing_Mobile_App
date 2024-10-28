package com.example.drawing_app.network

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ApiRepository(private val scope: CoroutineScope, private val apiService: ApiService) {
    val sharedImages: MutableLiveData<List<DrawingResponse>> = MutableLiveData(listOf())

    fun fetchSharedImages() {
        scope.launch {
            sharedImages.postValue(apiService.fetchSharedImages())
        }
    }

    fun shareImage(imageId: Int) {
        scope.launch {
            apiService.shareImage(imageId)
            fetchSharedImages() // Refresh shared images after sharing
        }
    }

    fun unshareImage(imageId: Int) {
        scope.launch {
            apiService.unshareImage(imageId)
            fetchSharedImages() // Refresh shared images after unsharing
        }
    }

    fun deleteImage(imageId: Int) {
        scope.launch {
            apiService.deleteImage(imageId)
            fetchSharedImages() // Refresh shared images after deletion
        }
    }

    fun uploadImage(userId: String, imageUrl: String) {
        scope.launch {
            apiService.uploadImage(ImageUploadRequest(userId, imageUrl))
            fetchSharedImages() // Refresh shared images after upload
        }
    }
}