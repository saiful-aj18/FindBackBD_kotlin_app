package com.saiful.findbackbd.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.saiful.findbackbd.data.local.ChatThreadEntity
import com.saiful.findbackbd.data.local.ItemDao
import com.saiful.findbackbd.data.local.MessageEntity
import com.saiful.findbackbd.data.local.toEntity
import com.saiful.findbackbd.data.model.ChatThread
import com.saiful.findbackbd.data.model.Message
import com.saiful.findbackbd.data.remote.toChatThreadOrNull
import com.saiful.findbackbd.data.remote.toMessageOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val itemDao: ItemDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val threadsFlow: Flow<List<ChatThread>> = itemDao.getAllThreads().map { list ->
        list.map { it.toDomain() }
    }

    private var chatsListener: ListenerRegistration? = null

    init {
        startChatsRealtimeListener()
        runCatching {
            auth.addAuthStateListener {
                startChatsRealtimeListener()
            }
        }
    }

    private fun startChatsRealtimeListener() {
        chatsListener?.remove()
        chatsListener = runCatching {
            db.collection("chats").addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val remoteThreads = snap.documents.mapNotNull { it.toChatThreadOrNull() }
                if (remoteThreads.isNotEmpty()) {
                    scope.launch {
                        itemDao.upsertAllThreads(remoteThreads.map { it.toEntity() })
                    }
                }
            }
        }.getOrNull()
    }

    fun messages(chatId: String): Flow<List<Message>> = callbackFlow {
        // Emit cached local messages immediately and keep observing Room + Firestore
        val roomJob = launch {
            itemDao.getMessagesForChat(chatId).collect { list ->
                trySend(list.map { it.toDomain() })
            }
        }

        val firestoreReg = runCatching {
            db.collection("chats").document(chatId).collection("messages")
                .addSnapshotListener { snap, err ->
                    if (err != null || snap == null) return@addSnapshotListener
                    val currentUid = runCatching { auth.currentUser?.uid }.getOrNull()
                    val currentName = runCatching { auth.currentUser?.displayName }.getOrNull()
                    val remoteMsgs = snap.documents
                        .mapNotNull { it.toMessageOrNull(chatId, currentUid, currentName) }
                        .sortedBy { it.timestamp }
                    if (remoteMsgs.isNotEmpty()) {
                        scope.launch {
                            itemDao.insertAllMessages(remoteMsgs.map { it.toEntity() })
                        }
                    }
                }
        }.getOrNull()

        awaitClose {
            firestoreReg?.remove()
            roomJob.cancel()
        }
    }

    suspend fun getOrCreateThread(
        participantName: String,
        participantPhone: String = "+880 1712 345678",
        itemId: String = "",
        itemName: String = ""
    ): ChatThread {
        val cleanName = participantName.trim().ifBlank { "Community Member" }
        val existingByName = itemDao.getThreadByParticipant(cleanName)
        if (existingByName != null) {
            val updated = if (itemName.isNotBlank() && existingByName.itemName.isBlank()) {
                existingByName.copy(itemId = itemId, itemName = itemName)
            } else {
                existingByName
            }
            itemDao.upsertThread(updated)
            return updated.toDomain()
        }

        val chatId = "chat_" + cleanName.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
        val existingById = itemDao.getThreadById(chatId)
        if (existingById != null) {
            return existingById.toDomain()
        }

        // Check Firestore before creating a new thread
        val remoteDoc = runCatching {
            withTimeoutOrNull(3000L) { db.collection("chats").document(chatId).get().await() }
        }.getOrNull()
        val remoteThread = remoteDoc?.toChatThreadOrNull()
        if (remoteThread != null) {
            itemDao.upsertThread(remoteThread.toEntity())
            return remoteThread
        }

        val now = System.currentTimeMillis()
        val timeLabel = formatCurrentTime(now)
        val newThread = ChatThreadEntity(
            id = chatId,
            participantName = cleanName,
            participantPhone = participantPhone,
            itemId = itemId,
            itemName = itemName,
            lastMessage = if (itemName.isNotBlank()) "Chat regarding $itemName" else "Start a conversation",
            lastTime = timeLabel,
            unreadCount = 0,
            updatedAt = now
        )
        itemDao.upsertThread(newThread)
        scope.launch {
            runCatching { db.collection("chats").document(chatId).set(newThread.toDomain()).await() }
        }
        return newThread.toDomain()
    }

    suspend fun markRead(chatId: String) {
        itemDao.markThreadRead(chatId)
    }

    suspend fun send(
        chatId: String,
        senderName: String,
        text: String,
        participantName: String,
        participantPhone: String = "+880 1712 345678",
        itemId: String = "",
        itemName: String = ""
    ): Result<Unit> = runCatching {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return@runCatching

        val now = System.currentTimeMillis()
        val timeLabel = formatCurrentTime(now)
        val senderId = runCatching { auth.currentUser?.uid }.getOrNull().orEmpty()
        val outgoing = Message(
            id = "msg_$now",
            chatId = chatId,
            senderName = senderName.ifBlank { "Me" },
            text = cleanText,
            time = timeLabel,
            mine = true,
            timestamp = now
        )

        itemDao.insertMessage(outgoing.toEntity())

        val existingThread = itemDao.getThreadById(chatId)
        val updatedThread = ChatThreadEntity(
            id = chatId,
            participantName = existingThread?.participantName ?: participantName,
            participantPhone = existingThread?.participantPhone ?: participantPhone,
            itemId = existingThread?.itemId?.ifBlank { itemId } ?: itemId,
            itemName = existingThread?.itemName?.ifBlank { itemName } ?: itemName,
            lastMessage = cleanText,
            lastTime = timeLabel,
            unreadCount = 0,
            updatedAt = now
        )
        itemDao.upsertThread(updatedThread)

        val msgMap = mapOf(
            "id" to outgoing.id,
            "chatId" to chatId,
            "senderId" to senderId,
            "senderName" to outgoing.senderName,
            "text" to outgoing.text,
            "time" to outgoing.time,
            "mine" to true,
            "timestamp" to outgoing.timestamp
        )

        runCatching {
            withTimeoutOrNull(5000L) {
                db.collection("chats").document(chatId).set(updatedThread.toDomain()).await()
                db.collection("chats").document(chatId).collection("messages")
                    .document(outgoing.id).set(msgMap).await()
            }
        }
    }

    private fun formatCurrentTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        return sdf.format(Date(timestamp))
    }
}
