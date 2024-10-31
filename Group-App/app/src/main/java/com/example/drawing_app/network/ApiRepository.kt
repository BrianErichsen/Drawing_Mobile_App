package com.example.drawing_app.network

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.drawing_app.ImageUploader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ApiRepository(private val scope: CoroutineScope, private val apiService: ApiService) {
    val sharedImages: MutableLiveData<List<DrawingResponse>> = MutableLiveData(listOf())
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    fun fetchSharedImages(userId: String) {
        scope.launch {
            try {
                val images = apiService.fetchSharedImages(userId)
                sharedImages.postValue(images)  // Update the LiveData with the fetched images
                Log.d("ApiRepository", "Fetched ${images.size} shared images")
            } catch (e: Exception) {
                Log.e("ApiRepository", "Failed to fetch shared images: ${e.message}")
            }
        }
    }

    fun shareImage(imageId: Int) {
        scope.launch {
            apiService.shareImage(imageId)
            fetchSharedImages(userId) // Refresh shared images after sharing
        }
    }

    fun unshareImage(imageId: Int) {
        scope.launch {
            apiService.unshareImage(imageId)
            fetchSharedImages(userId) // Refresh shared images after unsharing
        }
    }

    fun deleteImage(imageId: Int) {
        scope.launch {
            apiService.deleteImage(imageId)
            fetchSharedImages(userId) // Refresh shared images after deletion
        }
    }

    fun uploadImage(bitmap: Bitmap, userId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        scope.launch {
            ImageUploader.uploadImage(bitmap, userId, onSuccess = {
                Log.d("ApiRepository", it)
                fetchSharedImages(userId) // Refresh shared images after upload
                onSuccess(it)
            }, onError = {
                Log.e("ApiRepository", it)
                onError(it)
            })
        }
    }
    }