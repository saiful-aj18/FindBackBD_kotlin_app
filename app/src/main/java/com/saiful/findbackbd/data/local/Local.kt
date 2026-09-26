package com.saiful.findbackbd.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.saiful.findbackbd.data.model.AppNotification
import com.saiful.findbackbd.data.model.ChatThread
import com.saiful.findbackbd.data.model.FlaggedReport
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.model.Message
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.data.model.User
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val category: String,
    val description: String,
    val locationName: String,
    val date: String,
    val time: String,
    val distance: String = "1.2 km",
    val contact: String = "",
    val contactPhone: String = "+880 1712 345678",
    val contactEmail: String = "",
    val latitude: Double = 23.7808,
    val longitude: Double = 90.4071,
    val ownerId: String = "",
    val imageUrl: String = "",
    val isResolved: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): LostFoundItem = LostFoundItem(
        id = id,
        name = title,
        category = category,
        isLost = type.equals("lost", ignoreCase = true),
        place = locationName,
        time = time,
        distance = distance,
        description = description,
        contact = contact,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        date = date,
        latitude = latitude,
        longitude = longitude,
        ownerId = ownerId,
        imageUrl = imageUrl,
        isResolved = isResolved,
        updatedAt = updatedAt
    )
}

fun LostFoundItem.toEntity(): ItemEntity = ItemEntity(
    id = id,
    title = name,
    type = if (isLost) "lost" else "found",
    category = category,
    description = description,
    locationName = place,
    date = date,
    time = time,
    distance = distance,
    contact = contact,
    contactPhone = contactPhone,
    contactEmail = contactEmail,
    latitude = latitude,
    longitude = longitude,
    ownerId = ownerId,
    imageUrl = imageUrl,
    isResolved = isResolved,
    updatedAt = updatedAt
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String = "123456",
    val role: String = Role.USER.name,
    val isBlocked: Boolean = false,
    val avatarUrl: String = ""
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        role = runCatching { Role.valueOf(role) }.getOrDefault(Role.USER),
        isBlocked = isBlocked,
        avatarUrl = avatarUrl
    )
}

fun User.toEntity(password: String = "123456"): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    phone = phone,
    password = password,
    role = role.name,
    isBlocked = isBlocked,
    avatarUrl = avatarUrl
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val participantName: String,
    val participantPhone: String,
    val itemId: String,
    val itemName: String,
    val lastMessage: String,
    val lastTime: String,
    val unreadCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): ChatThread = ChatThread(
        id = id,
        participantName = participantName,
        participantPhone = participantPhone,
        itemId = itemId,
        itemName = itemName,
        lastMessage = lastMessage,
        lastTime = lastTime,
        unreadCount = unreadCount,
        updatedAt = updatedAt
    )
}

fun ChatThread.toEntity(): ChatThreadEntity = ChatThreadEntity(
    id = id,
    participantName = participantName,
    participantPhone = participantPhone,
    itemId = itemId,
    itemName = itemName,
    lastMessage = lastMessage,
    lastTime = lastTime,
    unreadCount = unreadCount,
    updatedAt = updatedAt
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderName: String,
    val text: String,
    val time: String,
    val mine: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): Message = Message(
        id = id,
        chatId = chatId,
        senderName = senderName,
        text = text,
        time = time,
        mine = mine,
        timestamp = timestamp
    )
}

fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    chatId = chatId,
    senderName = senderName,
    text = text,
    time = time,
    mine = mine,
    timestamp = timestamp
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val time: String,
    val type: String,
    val itemId: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): AppNotification = AppNotification(
        id = id,
        title = title,
        body = body,
        time = time,
        type = type,
        itemId = itemId,
        isRead = isRead,
        timestamp = timestamp
    )
}

fun AppNotification.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    title = title,
    body = body,
    time = time,
    type = type,
    itemId = itemId,
    isRead = isRead,
    timestamp = timestamp
)

@Entity(tableName = "flagged_reports")
data class FlaggedReportEntity(
    @PrimaryKey val id: String,
    val itemId: String,
    val itemName: String,
    val reason: String,
    val reporterName: String,
    val time: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): FlaggedReport = FlaggedReport(
        id = id,
        itemId = itemId,
        itemName = itemName,
        reason = reason,
        reporterName = reporterName,
        time = time,
        timestamp = timestamp
    )
}

fun FlaggedReport.toEntity(): FlaggedReportEntity = FlaggedReportEntity(
    id = id,
    itemId = itemId,
    itemName = itemName,
    reason = reason,
    reporterName = reporterName,
    time = time,
    timestamp = timestamp
)

@Dao
interface ItemDao {
    // Items
    @Query("SELECT * FROM items ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY updatedAt DESC")
    suspend fun getAllSnapshot(): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ItemEntity?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ItemEntity?

    @Query("SELECT COUNT(*) FROM items")
    suspend fun countItems(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM items")
    suspend fun clear()

    // Users
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:query) OR phone = :query LIMIT 1")
    suspend fun findUserByEmailOrPhone(query: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllUsers(users: List<UserEntity>)

    // Chat Threads
    @Query("SELECT * FROM chat_threads ORDER BY updatedAt DESC")
    fun getAllThreads(): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM chat_threads WHERE id = :id LIMIT 1")
    suspend fun getThreadById(id: String): ChatThreadEntity?

    @Query("SELECT * FROM chat_threads WHERE LOWER(participantName) = LOWER(:name) LIMIT 1")
    suspend fun getThreadByParticipant(name: String): ChatThreadEntity?

    @Query("SELECT COUNT(*) FROM chat_threads")
    suspend fun countThreads(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertThread(thread: ChatThreadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllThreads(threads: List<ChatThreadEntity>)

    @Query("UPDATE chat_threads SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markThreadRead(chatId: String)

    // Messages
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages")
    suspend fun countMessages(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(messages: List<MessageEntity>)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun countNotifications(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    // Flagged Reports
    @Query("SELECT * FROM flagged_reports ORDER BY timestamp DESC")
    fun getAllFlaggedReports(): Flow<List<FlaggedReportEntity>>

    @Query("SELECT COUNT(*) FROM flagged_reports")
    suspend fun countFlaggedReports(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlaggedReport(report: FlaggedReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFlaggedReports(reports: List<FlaggedReportEntity>)

    @Query("DELETE FROM flagged_reports WHERE id = :id")
    suspend fun deleteFlaggedReport(id: String)

    @Query("DELETE FROM flagged_reports WHERE itemId = :itemId")
    suspend fun deleteFlaggedReportsByItemId(itemId: String)
}

@Database(
    entities = [
        ItemEntity::class,
        UserEntity::class,
        ChatThreadEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        FlaggedReportEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
