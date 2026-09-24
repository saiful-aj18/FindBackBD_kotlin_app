package com.saiful.findbackbd.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.repository.AuthRepository
import com.saiful.findbackbd.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repo: ItemRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<String?>(null)
    val state: StateFlow<String?> = _state

    fun create(
        type: String,        // "lost" or "found" -> mapped to isLost below
        title: String,        // -> name
        category: String,
        description: String,
        location: String,     // -> place
        date: String,
        time: String,
        contact: String = ""  // was missing before; pass the reporter's contact info here
    ) {
        viewModelScope.launch {
            val uid = auth.currentUid() ?: return@launch
            val r = repo.create(
                LostFoundItem(
                    ownerId = uid,
                    name = title,
                    category = category,
                    isLost = type.equals("lost", ignoreCase = true),
                    place = location,
                    date = date,
                    time = time,
                    description = description,
                    contact = contact
                )
            )
            _state.value = r.fold({ "Created:$it" }, { it.message ?: "Failed" })
        }
    }
}