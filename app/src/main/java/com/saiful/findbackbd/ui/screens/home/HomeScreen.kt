package com.saiful.findbackbd.ui.screens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.ui.components.ItemCard
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.TextGray
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SearchScreen(
    onCreate: () -> Unit,
    onDetails: (String) -> Unit,
    onMap: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val items by vm.items.collectAsState()
    val query by vm.searchQuery.collectAsState()
    val selectedCategory by vm.selectedCategory.collectAsState()
    val selectedStatus by vm.selectedStatus.collectAsState()

    val filtered = remember(items, query, selectedCategory, selectedStatus) {
        val q = query.trim().lowercase()
        items.filter { item ->
            val matchesQuery = q.isBlank() ||
                item.name.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.place.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.contact.lowercase().contains(q)
            val matchesCategory = selectedCategory == "All" ||
                item.category.equals(selectedCategory, ignoreCase = true)
            val matchesStatus = when (selectedStatus) {
                "Lost" -> item.isLost && !item.isResolved
                "Found" -> !item.isLost && !item.isResolved
                "Resolved" -> item.isResolved
                else -> true
            }
            matchesQuery && matchesCategory && matchesStatus
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreate,
                containerColor = Green,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Report")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Search & Explore",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Find what was lost. Return what was found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = vm::setSearchQuery,
                    placeholder = { Text("Search by item name, place, or person...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Green) },
                    trailingIcon = if (query.isNotBlank()) {
                        {
                            IconButton(onClick = { vm.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = Color(0xFFDCE5E1),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Lost", "Found", "Resolved").forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { vm.setSelectedStatus(status) },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Green,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (listOf("All") + SampleData.categories).forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { vm.setSelectedCategory(category) },
                            label = { Text(category, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Green.copy(alpha = 0.15f),
                                selectedLabelColor = Green
                            )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onMap,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Map")
                    Spacer(Modifier.width(8.dp))
                    Text("View ${filtered.size} Reports on Interactive Map")
                }
            }
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No matching reports found", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Try clearing filters or submit a new report.", color = TextGray, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { item ->
                    ItemCard(item = item, onClick = { onDetails(item.id) })
                }
            }
        }
    }
}
