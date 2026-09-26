package com.saiful.findbackbd.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saiful.findbackbd.data.local.ItemDao
import com.saiful.findbackbd.data.local.NotificationEntity
import com.saiful.findbackbd.data.local.toEntity
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.data.model.User
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

    init {
        scope.launch {
            if (itemDao.countUsers() == 0) {
                val seeded = SampleData.users.map { user ->
                    val pwd = if (user.role == Role.ADMIN) "admin123" else "123456"
                    user.toEntity(password = pwd)
                }
                itemDao.upsertAllUsers(seeded)
            }
            val savedId = prefs.getString("logged_in_user_id", null)
            if (!savedId.isNullOrBlank()) {
                val localUser = itemDao.getUserById(savedId)?.toDomain()
                if (localUser != null && !localUser.isBlocked) {
                    _currentUser.value = localUser
                }
            }
            // Background sync of remote Firestore users into Room
            runCatching {
                val snap = withTimeoutOrNull(4000L) { db.collection("users").get().await() }
                val remoteUsers = snap?.toObjects(User::class.java).orEmpty()
                remoteUsers.forEach { remoteUser ->
                    if (remoteUser.id.isNotBlank()) {
                        val existing = itemDao.getUserById(remoteUser.id)
                        itemDao.upsertUser(remoteUser.toEntity(password = existing?.password ?: "123456"))
                    }
                }
            }
        }
    }

    fun currentUid(): String? = _currentUser.value?.id ?: runCatching { auth.currentUser?.uid }.getOrNull()

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> = runCatching {
        val cleanEmail = email.trim()
        val cleanName = name.trim()
        val cleanPhone = phone.trim().ifBlank { "+880 1700 000000" }

        val existing = itemDao.findUserByEmailOrPhone(cleanEmail)
        if (existing != null) {
            error("An account with this email or phone already exists. Please login.")
        }

        val firebaseUid = withTimeoutOrNull(2500L) {
            runCatching {
                auth.createUserWithEmailAndPassword(cleanEmail, password).await().user?.uid
            }.getOrNull()
        }

        val uid = firebaseUid ?: "user_${System.currentTimeMillis()}"
        val role = if (cleanEmail.equals("admin@findback.bd", ignoreCase = true)) Role.ADMIN else Role.USER
        val user = User(
            id = uid,
            name = cleanName,
            email = cleanEmail,
            phone = cleanPhone,
            role = role,
            isBlocked = false
        )

        itemDao.upsertUser(user.toEntity(password = password))
        saveSession(user)
        _currentUser.value = user

        scope.launch {
            runCatching { db.collection("users").document(uid).set(user).await() }
        }

        itemDao.insertNotification(
            NotificationEntity(
                id = "notif_welcome_${System.currentTimeMillis()}",
                title = "Welcome to FindBack BD, ${user.name}!",
                body = "Your account (${user.email}) is active. You can now report lost or found items and chat directly.",
                time = "Just now",
                type = "welcome",
                timestamp = System.currentTimeMillis()
            )
        )

        user
    }

    suspend fun login(
        emailOrPhone: String,
        password: String,
        requestedRole: Role = Role.USER
    ): Result<User> = runCatching {
        val cleanInput = emailOrPhone.trim()
        val isAdminCreds = cleanInput.equals("admin@findback.bd", ignoreCase = true) && password == "admin123"

        if (requestedRole == Role.ADMIN) {
            val existingAdmin = itemDao.findUserByEmailOrPhone(cleanInput)
            if (!isAdminCreds && (existingAdmin == null || existingAdmin.role != Role.ADMIN.name || existingAdmin.password != password)) {
                error("Invalid admin credentials. Use admin@findback.bd / admin123")
            }
            val adminUser = existingAdmin?.toDomain() ?: User(
                id = "admin_1",
                name = "FindBack Admin",
                email = "admin@findback.bd",
                phone = "+880 1700 000000",
                role = Role.ADMIN
            )
            itemDao.upsertUser(adminUser.toEntity(password = password))
            saveSession(adminUser)
            _currentUser.value = adminUser
            return@runCatching adminUser
        }

        if (isAdminCreds) {
            error("This is an administrator account. Please switch to Admin Login.")
        }

        val existingEntity = itemDao.findUserByEmailOrPhone(cleanInput)
        if (existingEntity != null) {
            if (existingEntity.isBlocked) {
                error("This account has been blocked by an administrator.")
            }
            if (existingEntity.role == Role.ADMIN.name) {
                error("This is an administrator account. Please switch to Admin Login.")
            }
            if (existingEntity.password.isNotBlank() && existingEntity.password != password) {
                error("Incorrect password for $cleanInput. Please try again.")
            }
            val domainUser = existingEntity.toDomain()
            saveSession(domainUser)
            _currentUser.value = domainUser
            scope.launch {
                runCatching { auth.signInWithEmailAndPassword(cleanInput, password).await() }
            }
            return@runCatching domainUser
        }

        val derivedName = deriveNameFromInput(cleanInput)
        val isPhoneInput = cleanInput.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
        val newUser = User(
            id = "user_${System.currentTimeMillis()}",
            name = derivedName,
            email = if (isPhoneInput) "${derivedName.lowercase(Locale.ROOT).replace(" ", ".")}@findback.bd" else cleanInput,
            phone = if (isPhoneInput) cleanInput else "+880 1712 345678",
            role = Role.USER,
            isBlocked = false
        )
        itemDao.upsertUser(newUser.toEntity(password = password))
        saveSession(newUser)
        _currentUser.value = newUser
        scope.launch {
            runCatching { db.collection("users").document(newUser.id).set(newUser).await() }
        }
        newUser
    }

    suspend fun loginWithGoogle(emailHint: String = ""): Result<User> = runCatching {
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
                runCatching { db.collection("users").document(it.id).set(it).await() }
            }
        }
        if (user.isBlocked) {
            error("This account has been blocked by an administrator.")
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
        itemDao.upsertUser(updated.toEntity(password = existingEntity?.password ?: "123456"))
        saveSession(updated)
        _currentUser.value = updated
        scope.launch {
            runCatching { db.collection("users").document(updated.id).set(updated).await() }
        }
        updated
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        val current = _currentUser.value ?: error("Not logged in")
        val entity = itemDao.getUserById(current.id) ?: error("Account not found")
        if (entity.password != currentPassword) {
            error("Current password does not match.")
        }
        if (newPassword.length < 6) {
            error("New password must be at least 6 characters.")
        }
        itemDao.upsertUser(entity.copy(password = newPassword))
    }

    suspend fun setUserBlocked(userId: String, blocked: Boolean) {
        val entity = itemDao.getUserById(userId) ?: return
        val updated = entity.copy(isBlocked = blocked)
        itemDao.upsertUser(updated)
        scope.launch {
            runCatching { db.collection("users").document(userId).set(updated.toDomain()).await() }
        }
        if (_currentUser.value?.id == userId && blocked) {
            logout()
        }
    }

    suspend fun reset(email: String): Result<Unit> = runCatching {
        val clean = email.trim()
        val existing = itemDao.findUserByEmailOrPhone(clean)
        if (existing != null) {
            itemDao.upsertUser(existing.copy(password = "123456"))
        }
        scope.launch {
            runCatching { auth.sendPasswordResetEmail(clean).await() }
        }
    }

    suspend fun getCurrentUser(): User? {
        _currentUser.value?.let { return it }
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
        if (raw.isBlank()) return "Community Member"
        return raw.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
            }
    }
}
