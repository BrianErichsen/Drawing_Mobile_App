package com.example.drawing_app.network

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    val sharedImages: LiveData<List<DrawingResponse>> = repository.sharedImages
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    fun fetchSharedImages(userId: String) {
        viewModelScope.launch {
            Log.d("ApiViewModel", "Fetching shared images from repository")
            repository.fetchSharedImages(userId)
        }
    }

    // Upload image using the repository
    fun uploadImage(bitmap: Bitmap, userId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        repository.uploadImage(bitmap, userId, onSuccess, onError)
    }

    // Additional functions for managing images
    fun shareImage(imageId: Int) {
        viewModelScope.launch {
            repository.shareImage(imageId)
            fetchSharedImages(userId) // Refresh after sharing
        }
    }

    fun unshareImage(imageId: Int) {
        viewModelScope.launch {
            repository.unshareImage(imageId)
            fetchSharedImages(userId) // Refresh after unsharing
        }
    }

    fun deleteImage(imageId: Int) {
        viewModelScope.launch {
            repository.deleteImage(imageId)
            fetchSharedImages(userId) // Refresh after deletion
        }
    }
}
