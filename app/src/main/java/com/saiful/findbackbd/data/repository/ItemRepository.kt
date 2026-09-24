package com.saiful.findbackbd.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saiful.findbackbd.data.model.LostFoundItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemRepository @Inject constructor(private val db: FirebaseFirestore) {

 suspend fun create(item: LostFoundItem): Result<String> = try {
  val ref = db.collection("items").document()
  db.collection("items").document(ref.id).set(item.copy(id = ref.id)).await()
  Result.success(ref.id)
 } catch (e: Exception) {
  Result.failure(e)
 }

 suspend fun getAll(): List<LostFoundItem> = try {
  db.collection("items").get().await().toObjects(LostFoundItem::class.java)
 } catch (_: Exception) {
  emptyList()
 }

 suspend fun get(id: String): LostFoundItem? = try {
  db.collection("items").document(id).get().await().toObject(LostFoundItem::class.java)
 } catch (_: Exception) {
  null
 }

 suspend fun update(item: LostFoundItem) = try {
  db.collection("items").document(item.id).set(item.copy(updatedAt = System.currentTimeMillis())).await()
  Result.success(Unit)
 } catch (e: Exception) {
  Result.failure(e)
 }

 suspend fun delete(id: String) = try {
  db.collection("items").document(id).delete().await()
  Result.success(Unit)
 } catch (e: Exception) {
  Result.failure(e)
 }
}