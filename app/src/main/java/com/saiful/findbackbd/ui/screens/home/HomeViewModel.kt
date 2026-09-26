package com.saiful.findbackbd.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.findbackbd.data.model.AppNotification
import com.saiful.findbackbd.data.model.ChatThread
import com.saiful.findbackbd.data.model.FlaggedReport
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.model.Message
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.data.model.User
import com.saiful.findbackbd.data.repository.AuthRepository
import com.saiful.findbackbd.data.repository.ChatRepository
import com.saiful.findbackbd.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ItemRepository,
    private val authRepo: AuthRepository,
    private val chatRepo: ChatRepository
) : ViewModel() {

    val items: StateFlow<List<LostFoundItem>> = repo.itemsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SampleData.items
    )

    val currentUser: StateFlow<User?> = authRepo.currentUser

    val allUsers: StateFlow<List<User>> = authRepo.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SampleData.users
    )

    val notifications: StateFlow<List<AppNotification>> = repo.notificationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SampleData.notifications
    )

    val flaggedReports: StateFlow<List<FlaggedReport>> = repo.flaggedReportsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SampleData.initialFlaggedReports
    )

    val chatThreads: StateFlow<List<ChatThread>> = chatRepo.threadsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SampleData.initialThreads
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedStatus = MutableStateFlow("All")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    fun load() {
        // Reactive Room Flows automatically emit latest items
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

    fun observeItem(id: String): Flow<LostFoundItem?> = repo.observeById(id)

    fun markResolved(itemId: String, resolved: Boolean = true) {
        viewModelScope.launch {
            repo.markResolved(itemId, resolved)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repo.delete(itemId)
        }
    }

    fun flagItem(item: LostFoundItem, reason: String) {
        viewModelScope.launch {
            val reporter = currentUser.value?.name ?: "Community Member"
            repo.flagItem(item, reason, reporter)
        }
    }

    fun dismissFlag(flagId: String) {
        viewModelScope.launch {
            repo.dismissFlag(flagId)
        }
    }

    fun removeFlaggedItem(flag: FlaggedReport) {
        viewModelScope.launch {
            repo.removeFlaggedItem(flag)
        }
    }

    fun setUserBlocked(userId: String, blocked: Boolean) {
        viewModelScope.launch {
            authRepo.setUserBlocked(userId, blocked)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repo.markAllNotificationsRead()
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repo.markNotificationRead(id)
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repo.deleteNotification(id)
        }
    }

    fun startChat(
        participantName: String,
        participantPhone: String = "+880 1712 345678",
        itemId: String = "",
        itemName: String = "",
        onReady: (ChatThread) -> Unit
    ) {
        viewModelScope.launch {
            val thread = chatRepo.getOrCreateThread(
                participantName = participantName,
                participantPhone = participantPhone,
                itemId = itemId,
                itemName = itemName
            )
            onReady(thread)
        }
    }

    fun messagesForChat(chatId: String): Flow<List<Message>> = chatRepo.messages(chatId)

    fun markChatRead(chatId: String) {
        viewModelScope.launch {
            chatRepo.markRead(chatId)
        }
    }

    fun sendMessage(
        chatId: String,
        text: String,
        participantName: String,
        participantPhone: String = "+880 1712 345678",
        itemId: String = "",
        itemName: String = ""
    ) {
        viewModelScope.launch {
            val sender = currentUser.value?.name ?: "Me"
            chatRepo.send(
                chatId = chatId,
                senderName = sender,
                text = text,
                participantName = participantName,
                participantPhone = participantPhone,
                itemId = itemId,
                itemName = itemName
            )
        }
    }
}
