package com.saiful.findbackbd.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saiful.findbackbd.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
 private val auth: FirebaseAuth,
 private val db: FirebaseFirestore
) {
 suspend fun register(name: String, email: String, password: String, phone: String): Result<User> = try {
  val r = auth.createUserWithEmailAndPassword(email, password).await()
  val uid = r.user?.uid ?: error("User creation failed")
  val u = User(id = uid, name = name, email = email, phone = phone)
  db.collection("users").document(uid).set(u).await()
  Result.success(u)
 } catch (e: Exception) {
  Result.failure(e)
 }

 suspend fun login(email: String, password: String) = try {
  auth.signInWithEmailAndPassword(email, password).await()
  Result.success(Unit)
 } catch (e: Exception) {
  Result.failure(e)
 }

 suspend fun reset(email: String) = try {
  auth.sendPasswordResetEmail(email).await()
  Result.success(Unit)
 } catch (e: Exception) {
  Result.failure(e)
 }

 fun logout() {
  auth.signOut()
 }

 fun currentUid() = auth.currentUser?.uid

 suspend fun getCurrentUser(): User? =
  currentUid()?.let { db.collection("users").document(it).get().await().toObject(User::class.java) }
}