package com.saiful.findbackbd.ui.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.findbackbd.data.model.LostFoundItem
import com.saiful.findbackbd.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel class HomeViewModel @Inject constructor(private val repo:ItemRepository):ViewModel(){private val _items=MutableStateFlow<List<LostFoundItem>>(emptyList());val items:StateFlow<List<LostFoundItem>> = _items;fun load(){viewModelScope.launch{_items.value=repo.getAll()}}}
