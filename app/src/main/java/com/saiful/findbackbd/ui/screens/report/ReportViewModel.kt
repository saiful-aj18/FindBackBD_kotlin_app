package com.saiful.findbackbd.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.model.User
import com.saiful.findbackbd.data.repository.AuthRepository
import com.saiful.findbackbd.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repo: ItemRepository,
    private val auth: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    val currentUser: StateFlow<User?> = auth.currentUser

    fun create(
        type: String,
        title: String,
        category: String,
        description: String,
        location: String,
        date: String,
        time: String,
        contact: String = "",
        contactPhone: String = "",
        latitude: Double = 23.7808,
        longitude: Double = 90.4071,
        distance: String = "1.2 km",
        imageUrl: String = "",
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        viewModelScope.launch {
            val user = auth.currentUser.value ?: auth.getCurrentUser()
            val uid = user?.id ?: auth.currentUid() ?: "local_user"
            val reporterName = contact.trim().ifBlank { user?.name ?: "Community Member" }
            val reporterPhone = contactPhone.trim().ifBlank { user?.phone ?: "+880 1712 345678" }
            val reporterEmail = user?.email ?: ""

            val item = LostFoundItem(
                name = title.trim(),
                category = category.trim().ifBlank { "Others" },
                isLost = type.equals("lost", ignoreCase = true),
                place = location.trim().ifBlank { "Dhaka, Bangladesh" },
                time = time.trim().ifBlank { "Just now" },
                distance = distance,
                date = date.trim().ifBlank { "Today" },
                description = description.trim(),
                contact = reporterName,
                contactPhone = reporterPhone,
                contactEmail = reporterEmail,
                latitude = latitude,
                longitude = longitude,
                ownerId = uid,
                imageUrl = imageUrl,
                isResolved = false,
                updatedAt = System.currentTimeMillis()
            )
            repo.create(item)
                .onSuccess { id ->
                    _isSubmitting.value = false
                    _state.value = id
                    onSuccess(id)
                }
                .onFailure { err ->
                    _isSubmitting.value = false
                    _state.value = "Failed"
                    onError(err.message ?: "Failed to create report")
                }
        }
    }
}
