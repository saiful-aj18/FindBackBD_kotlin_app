package com.saiful.findbackbd.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saiful.findbackbd.data.local.FlaggedReportEntity
import com.saiful.findbackbd.data.local.ItemDao
import com.saiful.findbackbd.data.local.NotificationEntity
import com.saiful.findbackbd.data.local.toEntity
import com.saiful.findbackbd.data.model.AppNotification
import com.saiful.findbackbd.data.model.FlaggedReport
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.utils.MatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val itemDao: ItemDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val itemsFlow: Flow<List<LostFoundItem>> = itemDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    val notificationsFlow: Flow<List<AppNotification>> = itemDao.getAllNotifications().map { entities ->
        entities.map { it.toDomain() }
    }

    val flaggedReportsFlow: Flow<List<FlaggedReport>> = itemDao.getAllFlaggedReports().map { entities ->
        entities.map { it.toDomain() }
    }

    init {
        scope.launch {
            if (itemDao.countItems() == 0) {
                itemDao.upsertAll(SampleData.items.map { it.toEntity() })
            }
            if (itemDao.countNotifications() == 0) {
                itemDao.insertAllNotifications(SampleData.notifications.map { it.toEntity() })
            }
            if (itemDao.countFlaggedReports() == 0) {
                itemDao.insertAllFlaggedReports(SampleData.initialFlaggedReports.map { it.toEntity() })
            }
            // Background sync with Firebase Firestore
            runCatching {
                val snap = withTimeoutOrNull(4000L) { db.collection("items").get().await() }
                val remoteItems = snap?.toObjects(LostFoundItem::class.java).orEmpty()
                if (remoteItems.isNotEmpty()) {
                    itemDao.upsertAll(remoteItems.filter { it.id.isNotBlank() }.map { it.toEntity() })
                }
            }
        }
    }

    fun observeById(id: String): Flow<LostFoundItem?> =
        itemDao.observeById(id).map { it?.toDomain() }

    suspend fun create(item: LostFoundItem): Result<String> = runCatching {
        val now = System.currentTimeMillis()
        val generatedId = item.id.ifBlank { "item_$now" }
        val withId = item.copy(
            id = generatedId,
            time = item.time.ifBlank { "Just now" },
            date = item.date.ifBlank { "Today" },
            updatedAt = now
        )

        itemDao.upsertItem(withId.toEntity())

        // Non-blocking sync to Firebase Firestore
        scope.launch {
            runCatching { db.collection("items").document(generatedId).set(withId).await() }
        }

        val existingItems = itemDao.getAllSnapshot().map { it.toDomain() }
        val oppositeItems = existingItems.filter { it.id != withId.id && it.isLost != withId.isLost && !it.isResolved }
        val bestMatch = oppositeItems
            .map { candidate -> candidate to MatchUtils.score(withId, candidate) }
            .filter { (_, score) -> score >= 40 }
            .maxByOrNull { (_, score) -> score }

        if (bestMatch != null) {
            val (matchedItem, score) = bestMatch
            itemDao.insertNotification(
                NotificationEntity(
                    id = "notif_match_$now",
                    title = "Possible Match Found ($score% Match)",
                    body = "\"${matchedItem.name}\" at ${matchedItem.place} matches your ${if (withId.isLost) "lost" else "found"} report \"${withId.name}\".",
                    time = "Just now",
                    type = "match",
                    itemId = matchedItem.id,
                    isRead = false,
                    timestamp = now + 1L
                )
            )
        }

        itemDao.insertNotification(
            NotificationEntity(
                id = "notif_post_$now",
                title = "${if (withId.isLost) "Lost" else "Found"} Report Published",
                body = "Your report for \"${withId.name}\" (${withId.place}) is now live for the community.",
                time = "Just now",
                type = "status",
                itemId = withId.id,
                isRead = false,
                timestamp = now
            )
        )

        generatedId
    }

    suspend fun getAll(): List<LostFoundItem> {
        val local = itemDao.getAllSnapshot().map { it.toDomain() }
        if (local.isNotEmpty()) return local
        return SampleData.items
    }

    suspend fun get(id: String): LostFoundItem? {
        return itemDao.getById(id)?.toDomain()
    }

    suspend fun update(item: LostFoundItem): Result<Unit> = runCatching {
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        itemDao.upsertItem(updated.toEntity())
        scope.launch {
            runCatching { db.collection("items").document(updated.id).set(updated).await() }
        }
    }

    suspend fun markResolved(id: String, resolved: Boolean = true): Result<Unit> = runCatching {
        val existing = itemDao.getById(id)?.toDomain() ?: error("Item not found")
        val updated = existing.copy(isResolved = resolved, updatedAt = System.currentTimeMillis())
        itemDao.upsertItem(updated.toEntity())
        scope.launch {
            runCatching { db.collection("items").document(updated.id).set(updated).await() }
        }
        if (resolved) {
            val now = System.currentTimeMillis()
            itemDao.insertNotification(
                NotificationEntity(
                    id = "notif_resolved_$now",
                    title = "Item Marked as Recovered!",
                    body = "\"${existing.name}\" at ${existing.place} has been marked as resolved.",
                    time = "Just now",
                    type = "status",
                    itemId = existing.id,
                    isRead = false,
                    timestamp = now
                )
            )
        }
    }

    suspend fun delete(id: String): Result<Unit> = runCatching {
        itemDao.deleteById(id)
        itemDao.deleteFlaggedReportsByItemId(id)
        scope.launch {
            runCatching { db.collection("items").document(id).delete().await() }
        }
    }

    suspend fun flagItem(item: LostFoundItem, reason: String, reporterName: String): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        val flag = FlaggedReportEntity(
            id = "flag_$now",
            itemId = item.id,
            itemName = item.name,
            reason = reason.trim().ifBlank { "Reported for moderator review" },
            reporterName = reporterName.ifBlank { "Community Member" },
            time = "Just now",
            timestamp = now
        )
        itemDao.insertFlaggedReport(flag)
        scope.launch {
            runCatching { db.collection("flagged_reports").document(flag.id).set(flag.toDomain()).await() }
        }
    }

    suspend fun dismissFlag(flagId: String) {
        itemDao.deleteFlaggedReport(flagId)
        scope.launch {
            runCatching { db.collection("flagged_reports").document(flagId).delete().await() }
        }
    }

    suspend fun removeFlaggedItem(flag: FlaggedReport) {
        itemDao.deleteFlaggedReport(flag.id)
        if (flag.itemId.isNotBlank()) {
            itemDao.deleteById(flag.itemId)
            scope.launch {
                runCatching { db.collection("items").document(flag.itemId).delete().await() }
            }
        }
    }

    suspend fun markAllNotificationsRead() {
        itemDao.markAllNotificationsRead()
    }

    suspend fun markNotificationRead(id: String) {
        itemDao.markNotificationRead(id)
    }

    suspend fun deleteNotification(id: String) {
        itemDao.deleteNotification(id)
    }
}
