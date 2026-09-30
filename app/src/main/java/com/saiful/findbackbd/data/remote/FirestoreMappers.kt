package com.saiful.findbackbd.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.saiful.findbackbd.data.model.AppNotification
import com.saiful.findbackbd.data.model.ChatThread
import com.saiful.findbackbd.data.model.FlaggedReport
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.model.Message
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.data.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun Map<String, Any?>.stringOf(vararg keys: String, default: String = ""): String {
    for (k in keys) {
        val v = this[k]
        if (v != null) {
            val str = v.toString().trim()
            if (str.isNotBlank() && str != "null") return str
        }
    }
    return default
}

private fun Map<String, Any?>.doubleOf(vararg keys: String, default: Double): Double {
    for (k in keys) {
        when (val v = this[k]) {
            is Number -> return v.toDouble()
            is String -> v.toDoubleOrNull()?.let { return it }
        }
    }
    return default
}

private fun Map<String, Any?>.longOf(vararg keys: String, default: Long = System.currentTimeMillis()): Long {
    for (k in keys) {
        when (val v = this[k]) {
            is Number -> return v.toLong()
            is Timestamp -> return v.toDate().time
            is Date -> return v.time
            is String -> v.toLongOrNull()?.let { return it }
        }
    }
    return default
}

private fun Map<String, Any?>.boolOf(vararg keys: String): Boolean? {
    for (k in keys) {
        when (val v = this[k]) {
            is Boolean -> return v
            is Number -> return v.toInt() != 0
            is String -> {
                if (v.equals("true", ignoreCase = true) || v == "1") return true
                if (v.equals("false", ignoreCase = true) || v == "0") return false
            }
        }
    }
    return null
}

fun DocumentSnapshot.toLostFoundItemOrNull(): LostFoundItem? {
    val map = data ?: return null
    val docId = map.stringOf("id", "itemId", default = id).ifBlank { id }

    val itemName = map.stringOf("name", "title", "itemName", "item_name")
    val itemCategory = map.stringOf("category", "itemCategory", "cat", default = "Others")
    val itemDesc = map.stringOf("description", "desc", "details", "body")
    val itemPlace = map.stringOf("place", "location", "locationName", "address", "area", default = "Dhaka, Bangladesh")

    // Determine Lost vs Found from boolean field OR type/status string
    val explicitIsLost = map.boolOf("isLost", "lost")
    val typeStr = map.stringOf("type", "reportType", "status", "itemType").lowercase(Locale.ROOT)
    val isLost = when {
        explicitIsLost != null -> explicitIsLost
        typeStr.contains("found") -> false
        typeStr.contains("lost") -> true
        else -> true
    }

    val explicitResolved = map.boolOf("isResolved", "resolved", "isRecovered")
    val isResolved = explicitResolved ?: (typeStr == "resolved" || typeStr == "recovered")

    val updatedMs = map.longOf("updatedAt", "createdAt", "timestamp", default = System.currentTimeMillis())
    val defaultDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(updatedMs))
    val defaultTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date(updatedMs))

    val itemDate = map.stringOf("date", "lostDate", "foundDate", "reportDate", default = defaultDate)
    val itemTime = map.stringOf("time", "lostTime", "foundTime", "reportTime", default = defaultTime)
    val itemDist = map.stringOf("distance", default = "1.2 km")

    val rawContact = map.stringOf("contact", "contactName", "userName", "reporterName", "ownerName", "authorName", "user")
    val rawPhone = map.stringOf("contactPhone", "phone", "phoneNumber", "userPhone", "mobile", "contactNumber")
    val contactPhone = if (rawPhone.isNotBlank()) {
        rawPhone
    } else if (rawContact.any { it.isDigit() }) {
        rawContact
    } else {
        "+880 1712 345678"
    }
    val contactName = if (rawContact.isNotBlank() && !rawContact.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }) {
        rawContact
    } else {
        map.stringOf("userName", "reporterName", "ownerName", default = "Community Member")
    }
    val contactEmail = map.stringOf("contactEmail", "email", "userEmail")
    val ownerId = map.stringOf("ownerId", "userId", "uid", "createdBy", "authorId")

    val singleImg = map.stringOf("imageUrl", "image", "photoUrl", "thumbnail")
    val listImg = (map["images"] as? List<*>)?.firstOrNull()?.toString()?.trim().orEmpty()
    val imageUrl = singleImg.ifBlank { listImg }

    val lat = map.doubleOf("latitude", "lat", default = 23.7808)
    val lng = map.doubleOf("longitude", "lng", "lon", default = 90.4071)

    // If both name and description are empty, use category + location so the item is still visible
    val finalName = itemName.ifBlank { "$itemCategory (${if (isLost) "Lost" else "Found"})" }

    return LostFoundItem(
        id = docId,
        name = finalName,
        category = itemCategory,
        isLost = isLost,
        place = itemPlace,
        time = itemTime,
        distance = itemDist,
        description = itemDesc,
        contact = contactName,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        date = itemDate,
        latitude = lat,
        longitude = lng,
        ownerId = ownerId,
        imageUrl = imageUrl,
        isResolved = isResolved,
        updatedAt = updatedMs
    )
}

fun LostFoundItem.toFirestoreMap(): Map<String, Any> = mapOf(
    "id" to id,
    "name" to name,
    "title" to name,
    "category" to category,
    "isLost" to isLost,
    "type" to if (isLost) "lost" else "found",
    "place" to place,
    "location" to place,
    "locationName" to place,
    "time" to time,
    "distance" to distance,
    "description" to description,
    "contact" to contact,
    "userName" to contact,
    "contactPhone" to contactPhone,
    "phone" to contactPhone,
    "contactEmail" to contactEmail,
    "date" to date,
    "latitude" to latitude,
    "longitude" to longitude,
    "ownerId" to ownerId,
    "userId" to ownerId,
    "imageUrl" to imageUrl,
    "isResolved" to isResolved,
    "status" to when {
        isResolved -> "resolved"
        isLost -> "lost"
        else -> "found"
    },
    "updatedAt" to updatedAt
)

fun DocumentSnapshot.toUserOrNull(): User? {
    val map = data ?: return null
    val uid = map.stringOf("id", "uid", "userId", default = id).ifBlank { id }
    val email = map.stringOf("email", "userEmail")
    val name = map.stringOf("name", "fullName", "displayName", "username", default = email.substringBefore("@").ifBlank { "User" })
    val phone = map.stringOf("phone", "phoneNumber", "mobile", "contactPhone", default = "+880 1712 345678")
    val roleStr = map.stringOf("role", "userRole", default = "USER").uppercase(Locale.ROOT)
    val isAdminBool = map.boolOf("isAdmin", "admin") == true
    val role = if (isAdminBool || roleStr == "ADMIN" || email.equals("admin@findback.bd", ignoreCase = true)) {
        Role.ADMIN
    } else {
        Role.USER
    }
    val isBlocked = map.boolOf("isBlocked", "blocked", "disabled") ?: false
    val avatarUrl = map.stringOf("avatarUrl", "photoUrl", "profileImage", "imageUrl")

    return User(
        id = uid,
        name = name,
        email = email,
        phone = phone,
        role = role,
        isBlocked = isBlocked,
        avatarUrl = avatarUrl
    )
}

fun User.toFirestoreMap(): Map<String, Any> = mapOf(
    "id" to id,
    "uid" to id,
    "name" to name,
    "fullName" to name,
    "email" to email,
    "phone" to phone,
    "role" to role.name,
    "isBlocked" to isBlocked,
    "avatarUrl" to avatarUrl
)

fun DocumentSnapshot.toChatThreadOrNull(): ChatThread? {
    val map = data ?: return null
    val threadId = map.stringOf("id", "chatId", default = id).ifBlank { id }
    val participantName = map.stringOf("participantName", "otherUserName", "userName", "name", default = "User")
    val participantPhone = map.stringOf("participantPhone", "phone", default = "+880 1712 345678")
    val itemId = map.stringOf("itemId", "reportId")
    val itemName = map.stringOf("itemName", "reportTitle", "title")
    val lastMessage = map.stringOf("lastMessage", "lastMsg", "text", default = "Tap to view messages")
    val updatedAt = map.longOf("updatedAt", "timestamp", default = System.currentTimeMillis())
    val defaultTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date(updatedAt))
    val lastTime = map.stringOf("lastTime", "time", default = defaultTime)
    val unreadCount = (map["unreadCount"] as? Number)?.toInt() ?: 0

    return ChatThread(
        id = threadId,
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

fun DocumentSnapshot.toMessageOrNull(defaultChatId: String, currentUserId: String?, currentUserName: String?): Message? {
    val map = data ?: return null
    val msgId = map.stringOf("id", default = id).ifBlank { id }
    val chatId = map.stringOf("chatId", default = defaultChatId).ifBlank { defaultChatId }
    val senderName = map.stringOf("senderName", "sender", "userName", "fromName", default = "")
    val senderId = map.stringOf("senderId", "fromId", "uid")
    val text = map.stringOf("text", "message", "body", "content")
    if (text.isBlank()) return null

    val timestamp = map.longOf("timestamp", "createdAt", "sentAt", default = System.currentTimeMillis())
    val defaultTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date(timestamp))
    val time = map.stringOf("time", default = defaultTime)

    val explicitMine = map.boolOf("mine", "isMine")
    val isMine = when {
        !currentUserId.isNullOrBlank() && senderId == currentUserId -> true
        !currentUserName.isNullOrBlank() && senderName.equals(currentUserName, ignoreCase = true) -> true
        senderName.equals("Me", ignoreCase = true) -> true
        explicitMine != null -> explicitMine
        else -> false
    }

    return Message(
        id = msgId,
        chatId = chatId,
        senderName = senderName,
        text = text,
        time = time,
        mine = isMine,
        timestamp = timestamp
    )
}

fun DocumentSnapshot.toNotificationOrNull(): AppNotification? {
    val map = data ?: return null
    val notifId = map.stringOf("id", default = id).ifBlank { id }
    val title = map.stringOf("title", "heading", default = "Notification")
    val body = map.stringOf("body", "message", "description", "text")
    val timestamp = map.longOf("timestamp", "createdAt", default = System.currentTimeMillis())
    val defaultTime = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(timestamp))
    val time = map.stringOf("time", default = defaultTime)
    val type = map.stringOf("type", default = "info")
    val itemId = map.stringOf("itemId", "reportId")
    val isRead = map.boolOf("isRead", "read") ?: false

    return AppNotification(
        id = notifId,
        title = title,
        body = body,
        time = time,
        type = type,
        itemId = itemId,
        isRead = isRead,
        timestamp = timestamp
    )
}

fun DocumentSnapshot.toFlaggedReportOrNull(): FlaggedReport? {
    val map = data ?: return null
    return FlaggedReport(
        id = map.stringOf("id", default = id).ifBlank { id },
        itemId = map.stringOf("itemId", "reportId"),
        itemName = map.stringOf("itemName", "title", default = "Reported Item"),
        reason = map.stringOf("reason", "description", default = "Flagged for review"),
        reporterName = map.stringOf("reporterName", "reportedBy", default = "User"),
        time = map.stringOf("time", default = "Recently"),
        timestamp = map.longOf("timestamp", "createdAt", default = System.currentTimeMillis())
    )
}
