package com.saiful.findbackbd.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.saiful.findbackbd.data.local.FlaggedReportEntity
import com.saiful.findbackbd.data.local.ItemDao
import com.saiful.findbackbd.data.local.NotificationEntity
import com.saiful.findbackbd.data.local.toEntity
import com.saiful.findbackbd.data.model.AppNotification
import com.saiful.findbackbd.data.model.FlaggedReport
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.remote.toFlaggedReportOrNull
import com.saiful.findbackbd.data.remote.toFirestoreMap
import com.saiful.findbackbd.data.remote.toLostFoundItemOrNull
import com.saiful.findbackbd.data.remote.toNotificationOrNull
import com.saiful.findbackbd.utils.MatchUtils
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val auth: FirebaseAuth,
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

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private var itemsListener: ListenerRegistration? = null
    private var reportsListener: ListenerRegistration? = null
    private var notifsListener: ListenerRegistration? = null
    private var flagsListener: ListenerRegistration? = null

    init {
        startRealtimeListeners()
        scope.launch {
            syncFromFirestore()
        }
        // Re-attach Firestore listeners whenever Firebase Auth user logs in or changes
        runCatching {
            auth.addAuthStateListener {
                startRealtimeListeners()
                scope.launch {
                    syncFromFirestore()
                }
            }
        }
    }

    fun startRealtimeListeners() {
        itemsListener?.remove()
        reportsListener?.remove()
        notifsListener?.remove()
        flagsListener?.remove()

        itemsListener = runCatching {
            db.collection("items").addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val remoteItems = snap.documents.mapNotNull { it.toLostFoundItemOrNull() }
                scope.launch {
                    if (remoteItems.isNotEmpty()) {
                        itemDao.upsertAll(remoteItems.map { it.toEntity() })
                    }
                }
            }
        }.getOrNull()

        // Also listen to "reports" collection in case items were saved under "reports" in Firestore
        reportsListener = runCatching {
            db.collection("reports").addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val remoteReports = snap.documents.mapNotNull { it.toLostFoundItemOrNull() }
                if (remoteReports.isNotEmpty()) {
                    scope.launch {
                        itemDao.upsertAll(remoteReports.map { it.toEntity() })
                    }
                }
            }
        }.getOrNull()

        notifsListener = runCatching {
            db.collection("notifications").addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val remoteNotifs = snap.documents.mapNotNull { it.toNotificationOrNull() }
                if (remoteNotifs.isNotEmpty()) {
                    scope.launch {
                        itemDao.insertAllNotifications(remoteNotifs.map { it.toEntity() })
                    }
                }
            }
        }.getOrNull()

        flagsListener = runCatching {
            db.collection("flagged_reports").addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val remoteFlags = snap.documents.mapNotNull { it.toFlaggedReportOrNull() }
                if (remoteFlags.isNotEmpty()) {
                    scope.launch {
                        itemDao.insertAllFlaggedReports(remoteFlags.map { it.toEntity() })
                    }
                }
            }
        }.getOrNull()
    }

    suspend fun syncFromFirestore(): List<LostFoundItem> {
        _isSyncing.value = true
        return try {
            val itemsSnap = runCatching {
                withTimeoutOrNull(8000L) { db.collection("items").get().await() }
            }.getOrNull()

            val reportsSnap = runCatching {
                withTimeoutOrNull(8000L) { db.collection("reports").get().await() }
            }.getOrNull()

            val combined = buildList {
                itemsSnap?.documents?.forEach { doc ->
                    doc.toLostFoundItemOrNull()?.let { add(it) }
                }
                reportsSnap?.documents?.forEach { doc ->
                    doc.toLostFoundItemOrNull()?.let { add(it) }
                }
            }.distinctBy { it.id }

            if (combined.isNotEmpty() || (itemsSnap != null && itemsSnap.isEmpty && reportsSnap != null && reportsSnap.isEmpty)) {
                itemDao.clear()
                if (combined.isNotEmpty()) {
                    itemDao.upsertAll(combined.map { it.toEntity() })
                }
            }

            itemDao.getAllSnapshot().map { it.toDomain() }
        } finally {
            _isSyncing.value = false
        }
    }

    fun observeById(id: String): Flow<LostFoundItem?> =
        itemDao.observeById(id).map { it?.toDomain() }

    suspend fun create(item: LostFoundItem): Result<String> = runCatching {
        val now = System.currentTimeMillis()
        val docRef = if (item.id.isBlank()) {
            db.collection("items").document()
        } else {
            db.collection("items").document(item.id)
        }
        val generatedId = docRef.id
        val withId = item.copy(
            id = generatedId,
            time = item.time.ifBlank { "Just now" },
            date = item.date.ifBlank { "Today" },
            updatedAt = now
        )

        // Save immediately to Room so UI updates right away
        itemDao.upsertItem(withId.toEntity())

        // Write to Firebase Firestore `items/{id}`
        runCatching {
            withTimeoutOrNull(6000L) {
                docRef.set(withId.toFirestoreMap()).await()
            }
        }

        // Check for smart matches against existing opposite-status items
        val existingItems = itemDao.getAllSnapshot().map { it.toDomain() }
        val oppositeItems = existingItems.filter { it.id != withId.id && it.isLost != withId.isLost && !it.isResolved }
        val bestMatch = oppositeItems
            .map { candidate -> candidate to MatchUtils.score(withId, candidate) }
            .filter { (_, score) -> score >= 40 }
            .maxByOrNull { (_, score) -> score }

        if (bestMatch != null) {
            val (matchedItem, score) = bestMatch
            val matchNotif = NotificationEntity(
                id = "notif_match_$now",
                title = "Possible Match Found ($score% Match)",
                body = "\"${matchedItem.name}\" at ${matchedItem.place} matches your ${if (withId.isLost) "lost" else "found"} report \"${withId.name}\".",
                time = "Just now",
                type = "match",
                itemId = matchedItem.id,
                isRead = false,
                timestamp = now + 1L
            )
            itemDao.insertNotification(matchNotif)
            scope.launch {
                runCatching { db.collection("notifications").document(matchNotif.id).set(matchNotif.toDomain()).await() }
            }
        }

        val postNotif = NotificationEntity(
            id = "notif_post_$now",
            title = "${if (withId.isLost) "Lost" else "Found"} Report Published",
            body = "Your report for \"${withId.name}\" (${withId.place}) is now live in Firebase.",
            time = "Just now",
            type = "status",
            itemId = withId.id,
            isRead = false,
            timestamp = now
        )
        itemDao.insertNotification(postNotif)
        scope.launch {
            runCatching { db.collection("notifications").document(postNotif.id).set(postNotif.toDomain()).await() }
        }

        generatedId
    }

    suspend fun getAll(): List<LostFoundItem> = syncFromFirestore()

    suspend fun get(id: String): LostFoundItem? {
        itemDao.getById(id)?.toDomain()?.let { return it }
        return runCatching {
            val doc = withTimeoutOrNull(5000L) { db.collection("items").document(id).get().await() }
            doc?.toLostFoundItemOrNull()?.also { itemDao.upsertItem(it.toEntity()) }
        }.getOrNull()
    }

    suspend fun update(item: LostFoundItem): Result<Unit> = runCatching {
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        itemDao.upsertItem(updated.toEntity())
        runCatching {
            withTimeoutOrNull(5000L) {
                db.collection("items").document(updated.id).set(updated.toFirestoreMap()).await()
            }
        }
    }

    suspend fun markResolved(id: String, resolved: Boolean = true): Result<Unit> = runCatching {
        val existing = itemDao.getById(id)?.toDomain() ?: get(id) ?: error("Item not found")
        val updated = existing.copy(isResolved = resolved, updatedAt = System.currentTimeMillis())
        itemDao.upsertItem(updated.toEntity())
        runCatching {
            withTimeoutOrNull(5000L) {
                db.collection("items").document(updated.id).set(updated.toFirestoreMap()).await()
            }
        }
        if (resolved) {
            val now = System.currentTimeMillis()
            val notif = NotificationEntity(
                id = "notif_resolved_$now",
                title = "Item Marked as Recovered!",
                body = "\"${existing.name}\" at ${existing.place} has been marked as resolved.",
                time = "Just now",
                type = "status",
                itemId = existing.id,
                isRead = false,
                timestamp = now
            )
            itemDao.insertNotification(notif)
            scope.launch {
                runCatching { db.collection("notifications").document(notif.id).set(notif.toDomain()).await() }
            }
        }
    }

    suspend fun delete(id: String): Result<Unit> = runCatching {
        itemDao.deleteById(id)
        itemDao.deleteFlaggedReportsByItemId(id)
        runCatching {
            withTimeoutOrNull(5000L) {
                db.collection("items").document(id).delete().await()
            }
        }
        runCatching {
            withTimeoutOrNull(3000L) {
                db.collection("reports").document(id).delete().await()
            }
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
        scope.launch {
            runCatching { db.collection("flagged_reports").document(flag.id).delete().await() }
        }
        if (flag.itemId.isNotBlank()) {
            delete(flag.itemId)
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
        scope.launch {
            runCatching { db.collection("notifications").document(id).delete().await() }
        }
    }
}
