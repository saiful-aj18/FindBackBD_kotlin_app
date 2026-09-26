package com.saiful.findbackbd.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saiful.findbackbd.data.local.ChatThreadEntity
import com.saiful.findbackbd.data.local.ItemDao
import com.saiful.findbackbd.data.local.MessageEntity
import com.saiful.findbackbd.data.local.NotificationEntity
import com.saiful.findbackbd.data.local.toEntity
import com.saiful.findbackbd.data.model.ChatThread
import com.saiful.findbackbd.data.model.Message
import com.saiful.findbackbd.data.model.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val itemDao: ItemDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val threadsFlow: Flow<List<ChatThread>> = itemDao.getAllThreads().map { list ->
        list.map { it.toDomain() }
    }

    init {
        scope.launch {
            if (itemDao.countThreads() == 0) {
                itemDao.upsertAllThreads(SampleData.initialThreads.map { it.toEntity() })
            }
            if (itemDao.countMessages() == 0) {
                itemDao.insertAllMessages(SampleData.messages.map { it.toEntity() })
            }
        }
    }

    fun messages(chatId: String): Flow<List<Message>> =
        itemDao.getMessagesForChat(chatId).map { list -> list.map { it.toDomain() } }

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

        val now = System.currentTimeMillis()
        val timeLabel = formatCurrentTime(now)
        val initialGreeting = if (itemName.isNotBlank()) {
            "Hi! Feel free to message me regarding \"$itemName\"."
        } else {
            "Hello! How can I help you with your lost or found report?"
        }

        val newThread = ChatThreadEntity(
            id = chatId,
            participantName = cleanName,
            participantPhone = participantPhone,
            itemId = itemId,
            itemName = itemName,
            lastMessage = initialGreeting,
            lastTime = timeLabel,
            unreadCount = 0,
            updatedAt = now
        )
        itemDao.upsertThread(newThread)
        itemDao.insertMessage(
            MessageEntity(
                id = "msg_init_$now",
                chatId = chatId,
                senderName = cleanName,
                text = initialGreeting,
                time = timeLabel,
                mine = false,
                timestamp = now
            )
        )
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
            lastMessage = "You: $cleanText",
            lastTime = timeLabel,
            unreadCount = 0,
            updatedAt = now
        )
        itemDao.upsertThread(updatedThread)

        scope.launch {
            runCatching {
                db.collection("chats").document(chatId).set(updatedThread.toDomain()).await()
                db.collection("chats").document(chatId).collection("messages").document(outgoing.id).set(outgoing).await()
            }
        }

        scope.launch {
            delay(1100L)
            val replyNow = System.currentTimeMillis()
            val replyTime = formatCurrentTime(replyNow)
            val targetParticipant = updatedThread.participantName.ifBlank { participantName }
            val replyText = buildContextualReply(cleanText, targetParticipant, updatedThread.itemName)

            val replyMsg = MessageEntity(
                id = "msg_reply_$replyNow",
                chatId = chatId,
                senderName = targetParticipant,
                text = replyText,
                time = replyTime,
                mine = false,
                timestamp = replyNow
            )
            itemDao.insertMessage(replyMsg)
            val afterReplyThread = updatedThread.copy(
                lastMessage = replyText,
                lastTime = replyTime,
                unreadCount = 0,
                updatedAt = replyNow
            )
            itemDao.upsertThread(afterReplyThread)
            itemDao.insertNotification(
                NotificationEntity(
                    id = "notif_chat_$replyNow",
                    title = "New Message from $targetParticipant",
                    body = replyText,
                    time = replyTime,
                    type = "message",
                    itemId = updatedThread.itemId,
                    isRead = false,
                    timestamp = replyNow
                )
            )
            runCatching {
                db.collection("chats").document(chatId).set(afterReplyThread.toDomain()).await()
                db.collection("chats").document(chatId).collection("messages").document(replyMsg.id).set(replyMsg.toDomain()).await()
            }
        }
    }

    private fun formatCurrentTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        return sdf.format(Date(timestamp))
    }

    private fun buildContextualReply(
        userMessage: String,
        participantName: String,
        itemName: String
    ): String {
        val lower = userMessage.lowercase(Locale.ROOT)
        val subject = if (itemName.isNotBlank()) "\"$itemName\"" else "the item"
        return when {
            lower.contains("where") || lower.contains("location") || lower.contains("meet") ->
                "We can meet at a safe public spot near the reported location to verify and hand over $subject."
            lower.contains("phone") || lower.contains("call") || lower.contains("number") || lower.contains("contact") ->
                "Sure! You can call me directly using the Call button at the top of this chat."
            lower.contains("available") || lower.contains("still") || lower.contains("found") ->
                "Yes, $subject is still with me. Can you share one identifying detail to confirm ownership?"
            lower.contains("thank") ->
                "You're very welcome! Glad we could connect on FindBack BD."
            else ->
                "Thanks for your message! I'm available today to coordinate regarding $subject."
        }
    }
}
