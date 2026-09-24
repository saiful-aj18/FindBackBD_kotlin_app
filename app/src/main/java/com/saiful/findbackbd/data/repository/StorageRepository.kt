package com.saiful.findbackbd.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepository @Inject constructor(private val storage: FirebaseStorage) {
    suspend fun uploadItemImage(uid: String, itemId: String, uri: Uri): Result<String> = try {
        val ref = storage.reference.child("item_images/$uid/$itemId/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).await()
        Result.success(ref.downloadUrl.await().toString())
    } catch (e: Exception) { Result.failure(e) }

    suspend fun uploadProfileImage(uid: String, uri: Uri): Result<String> = try {
        val ref = storage.reference.child("profile_images/$uid/profile.jpg")
        ref.putFile(uri).await()
        Result.success(ref.downloadUrl.await().toString())
    } catch (e: Exception) { Result.failure(e) }
}
