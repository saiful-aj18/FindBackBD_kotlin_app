package com.saiful.findbackbd.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.saiful.findbackbd.data.local.ItemDao
import com.saiful.findbackbd.data.local.toEntity
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.data.model.User
import com.saiful.findbackbd.data.remote.toFirestoreMap
import com.saiful.findbackbd.data.remote.toUserOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val itemDao: ItemDao,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs = context.getSharedPreferences("findback_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val allUsers: Flow<List<User>> = itemDao.getAllUsers().map { list ->
        list.map { it.toDomain() }
    }

    private var usersListener: ListenerRegistration? = null

    init {
        // Restore cached user immediately if available, then verify with Firebase Auth & Firestore
        scope.launch {
            val savedId = prefs.getString("logged_in_user_id", null)
            if (!savedId.isNullOrBlank()) {
                val localUser = itemDao.getUserById(savedId)?.toDomain()
                if (localUser != null && !localUser.isBlocked) {
                    _currentUser.value = localUser
                }
            }
            val fbUser = runCatching { auth.currentUser }.getOrNull()
            if (fbUser != null) {
                syncCurrentFirebaseUser(fbUser.uid, fbUser.email.orEmpty(), fbUser.displayName.orEmpty())
            }
            startUsersRealtimeListener()
        }

        runCatching {
            auth.addAuthStateListener { firebaseAuth ->
                val fbUser = firebaseAuth.currentUser
                if (fbUser != null) {
                    scope.launch {
                        syncCurrentFirebaseUser(fbUser.uid, fbUser.email.orEmpty(), fbUser.displayName.orEmpty())
                        startUsersRealtimeListener()
                    }
                }
            }
        }
    }

    private fun startUsersRealtimeListener() {
        usersListener?.remove()
        usersListener = runCatching {
            db.collection("users").addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val remoteUsers = snap.documents.mapNotNull { it.toUserOrNull() }
                if (remoteUsers.isNotEmpty()) {
                    scope.launch {
                        remoteUsers.forEach { u ->
                            val existing = itemDao.getUserById(u.id)
                            itemDao.upsertUser(u.toEntity(password = existing?.password ?: ""))
                        }
                        val activeId = _currentUser.value?.id ?: auth.currentUser?.uid
                        if (!activeId.isNullOrBlank()) {
                            remoteUsers.find { it.id == activeId }?.let { freshUser ->
                                _currentUser.value = freshUser
                            }
                        }
                    }
                }
            }
        }.getOrNull()
    }

    private suspend fun syncCurrentFirebaseUser(uid: String, email: String, displayName: String): User {
        val docUser = runCatching {
            val doc = withTimeoutOrNull(6000L) { db.collection("users").document(uid).get().await() }
            doc?.toUserOrNull()
        }.getOrNull() ?: runCatching {
            if (email.isNotBlank()) {
                val query = withTimeoutOrNull(6000L) {
                    db.collection("users").whereEqualTo("email", email).get().await()
                }
                query?.documents?.firstOrNull()?.toUserOrNull()
            } else null
        }.getOrNull()

        val resolvedUser = docUser ?: itemDao.getUserById(uid)?.toDomain() ?: User(
            id = uid,
            name = displayName.ifBlank { deriveNameFromInput(email) },
            email = email,
            phone = "+880 1712 345678",
            role = if (email.equals("admin@findback.bd", ignoreCase = true)) Role.ADMIN else Role.USER
        ).also { created ->
            runCatching {
                db.collection("users").document(uid).set(created.toFirestoreMap()).await()
            }
        }

        itemDao.upsertUser(resolvedUser.toEntity())
        saveSession(resolvedUser)
        _currentUser.value = resolvedUser
        return resolvedUser
    }

    fun currentUid(): String? = runCatching { auth.currentUser?.uid }.getOrNull() ?: _currentUser.value?.id

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> = runCatching {
        val cleanEmail = email.trim()
        val cleanName = name.trim()
        val cleanPhone = phone.trim().ifBlank { "+880 1700 000000" }

        // 1. Register user in Firebase Authentication
        val authResult = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
        val fbUser = authResult.user ?: error("Firebase user creation failed")
        runCatching {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(cleanName)
                .build()
            fbUser.updateProfile(profileUpdates).await()
        }

        val uid = fbUser.uid
        val role = if (cleanEmail.equals("admin@findback.bd", ignoreCase = true)) Role.ADMIN else Role.USER
        val user = User(
            id = uid,
            name = cleanName,
            email = cleanEmail,
            phone = cleanPhone,
            role = role,
            isBlocked = false
        )

        // 2. Save user document in Firebase Firestore `users/{uid}`
        runCatching {
            db.collection("users").document(uid).set(user.toFirestoreMap()).await()
        }

        // 3. Cache in local Room DB and set active session
        itemDao.upsertUser(user.toEntity(password = password))
        saveSession(user)
        _currentUser.value = user
        startUsersRealtimeListener()

        user
    }

    suspend fun login(
        emailOrPhone: String,
        password: String,
        requestedRole: Role = Role.USER
    ): Result<User> = runCatching {
        val cleanInput = emailOrPhone.trim()
        val isAdminDemo = cleanInput.equals("admin@findback.bd", ignoreCase = true) && password == "admin123"

        // 1. First authenticate against real Firebase Authentication
        val firebaseSignInResult = runCatching {
            auth.signInWithEmailAndPassword(cleanInput, password).await()
        }

        if (firebaseSignInResult.isSuccess) {
            val fbUser = firebaseSignInResult.getOrNull()?.user ?: error("Firebase login failed")
            val user = syncCurrentFirebaseUser(
                uid = fbUser.uid,
                email = fbUser.email ?: cleanInput,
                displayName = fbUser.displayName.orEmpty()
            )
            if (user.isBlocked) {
                auth.signOut()
                error("This account has been blocked by an administrator.")
            }
            if (requestedRole == Role.ADMIN && user.role != Role.ADMIN && !isAdminDemo) {
                error("This account does not have Admin privileges.")
            }
            val finalUser = if (requestedRole == Role.ADMIN) user.copy(role = Role.ADMIN) else user
            itemDao.upsertUser(finalUser.toEntity(password = password))
            saveSession(finalUser)
            _currentUser.value = finalUser
            startUsersRealtimeListener()
            return@runCatching finalUser
        }

        // 2. Support built-in Demo Admin login if not yet created in Firebase Auth
        if (requestedRole == Role.ADMIN && isAdminDemo) {
            val adminUser = User(
                id = "admin_1",
                name = "FindBack Admin",
                email = "admin@findback.bd",
                phone = "+880 1700 000000",
                role = Role.ADMIN
            )
            itemDao.upsertUser(adminUser.toEntity(password = password))
            saveSession(adminUser)
            _currentUser.value = adminUser
            startUsersRealtimeListener()
            return@runCatching adminUser
        }

        // 3. Also check if user document exists directly in Firestore `users` collection or local Room
        val firestoreUserDoc = runCatching {
            val byEmail = withTimeoutOrNull(5000L) {
                db.collection("users").whereEqualTo("email", cleanInput).get().await()
            }?.documents?.firstOrNull()
            val byPhone = if (byEmail == null) {
                withTimeoutOrNull(5000L) {
                    db.collection("users").whereEqualTo("phone", cleanInput).get().await()
                }?.documents?.firstOrNull()
            } else null
            (byEmail ?: byPhone)?.toUserOrNull()
        }.getOrNull()

        if (firestoreUserDoc != null) {
            if (firestoreUserDoc.isBlocked) {
                error("This account has been blocked by an administrator.")
            }
            itemDao.upsertUser(firestoreUserDoc.toEntity(password = password))
            saveSession(firestoreUserDoc)
            _currentUser.value = firestoreUserDoc
            startUsersRealtimeListener()
            return@runCatching firestoreUserDoc
        }

        val localEntity = itemDao.findUserByEmailOrPhone(cleanInput)
        if (localEntity != null && (localEntity.password == password || localEntity.password.isBlank())) {
            val domainUser = localEntity.toDomain()
            saveSession(domainUser)
            _currentUser.value = domainUser
            return@runCatching domainUser
        }

        // Surface the real Firebase Auth error so user knows why login failed
        val fbError = firebaseSignInResult.exceptionOrNull()?.localizedMessage
            ?: "Login failed. Please check your email and password or register a new account."
        error(fbError)
    }

    suspend fun loginWithGoogle(emailHint: String = ""): Result<User> = runCatching {
        val fbCurrent = auth.currentUser
        if (fbCurrent != null) {
            return@runCatching syncCurrentFirebaseUser(
                uid = fbCurrent.uid,
                email = fbCurrent.email.orEmpty(),
                displayName = fbCurrent.displayName.orEmpty()
            )
        }
        val targetEmail = emailHint.trim().ifBlank { "user.google@gmail.com" }
        val existing = itemDao.findUserByEmailOrPhone(targetEmail)
        val user = existing?.toDomain() ?: User(
            id = "google_${System.currentTimeMillis()}",
            name = deriveNameFromInput(targetEmail),
            email = targetEmail,
            phone = "+880 1711 998877",
            role = Role.USER
        ).also {
            itemDao.upsertUser(it.toEntity(password = "google_auth"))
            scope.launch {
                runCatching { db.collection("users").document(it.id).set(it.toFirestoreMap()).await() }
            }
        }
        saveSession(user)
        _currentUser.value = user
        user
    }

    suspend fun updateProfile(name: String, email: String, phone: String): Result<User> = runCatching {
        val current = _currentUser.value ?: error("Not logged in")
        val existingEntity = itemDao.getUserById(current.id)
        val updated = current.copy(
            name = name.trim().ifBlank { current.name },
            email = email.trim().ifBlank { current.email },
            phone = phone.trim().ifBlank { current.phone }
        )
        itemDao.upsertUser(updated.toEntity(password = existingEntity?.password ?: ""))
        saveSession(updated)
        _currentUser.value = updated
        runCatching {
            withTimeoutOrNull(4000L) {
                db.collection("users").document(updated.id).set(updated.toFirestoreMap()).await()
            }
        }
        updated
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        if (newPassword.length < 6) {
            error("New password must be at least 6 characters.")
        }
        val fbUser = auth.currentUser
        if (fbUser != null) {
            runCatching { fbUser.updatePassword(newPassword).await() }
        }
        val current = _currentUser.value
        if (current != null) {
            val entity = itemDao.getUserById(current.id)
            if (entity != null) {
                itemDao.upsertUser(entity.copy(password = newPassword))
            }
        }
    }

    suspend fun setUserBlocked(userId: String, blocked: Boolean) {
        val entity = itemDao.getUserById(userId) ?: return
        val updated = entity.copy(isBlocked = blocked)
        itemDao.upsertUser(updated)
        scope.launch {
            runCatching { db.collection("users").document(userId).set(updated.toDomain().toFirestoreMap()).await() }
        }
        if (_currentUser.value?.id == userId && blocked) {
            logout()
        }
    }

    suspend fun reset(email: String): Result<Unit> = runCatching {
        val clean = email.trim()
        auth.sendPasswordResetEmail(clean).await()
    }

    suspend fun getCurrentUser(): User? {
        _currentUser.value?.let { return it }
        val fbUser = runCatching { auth.currentUser }.getOrNull()
        if (fbUser != null) {
            return syncCurrentFirebaseUser(fbUser.uid, fbUser.email.orEmpty(), fbUser.displayName.orEmpty())
        }
        val savedId = prefs.getString("logged_in_user_id", null)
        if (!savedId.isNullOrBlank()) {
            val local = itemDao.getUserById(savedId)?.toDomain()
            if (local != null) {
                _currentUser.value = local
                return local
            }
        }
        return null
    }

    fun logout() {
        prefs.edit().remove("logged_in_user_id").apply()
        _currentUser.value = null
        runCatching { auth.signOut() }
    }

    private fun saveSession(user: User) {
        prefs.edit().putString("logged_in_user_id", user.id).apply()
    }

    private fun deriveNameFromInput(input: String): String {
        val raw = input.substringBefore("@")
            .replace(Regex("[^a-zA-Z.\\s_-]"), "")
            .replace('.', ' ')
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
        if (raw.isBlank()) return "User"
        return raw.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
            }
    }
}
