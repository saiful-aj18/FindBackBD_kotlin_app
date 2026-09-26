package com.saiful.findbackbd.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadItemImage(userId: String, itemId: String, uri: Uri): Result<String> = runCatching {
        val ref = storage.reference.child("items/$userId/$itemId/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun uploadProfileImage(userId: String, uri: Uri): Result<String> = runCatching {
        val ref = storage.reference.child("users/$userId/profile.jpg")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }
}
