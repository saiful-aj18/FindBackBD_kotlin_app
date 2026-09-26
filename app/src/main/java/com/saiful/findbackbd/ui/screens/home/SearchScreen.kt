package com.saiful.findbackbd.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.ui.components.CategoryCard
import com.saiful.findbackbd.ui.components.ItemCard
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenDark
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun HomeScreen(
    onItem: (String) -> Unit,
    onSearch: () -> Unit,
    onCreate: () -> Unit,
    onNotifs: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val allItems by vm.items.collectAsState()
    val currentUser by vm.currentUser.collectAsState()
    val notifications by vm.notifications.collectAsState()
    val unreadNotifCount = notifications.count { !it.isRead }

    var activeCategory by remember { mutableStateOf("All") }
    var activeTab by remember { mutableStateOf("All") }

    val filteredItems = remember(allItems, activeCategory, activeTab) {
        allItems.filter { item ->
            val matchesCat = activeCategory == "All" || item.category.equals(activeCategory, ignoreCase = true)
            val matchesTab = when (activeTab) {
                "Lost" -> item.isLost && !item.isResolved
                "Found" -> !item.isLost && !item.isResolved
                "Resolved" -> item.isResolved
                else -> true
            }
            matchesCat && matchesTab
        }
    }

    val lostCount = allItems.count { it.isLost && !it.isResolved }
    val foundCount = allItems.count { !it.isLost && !it.isResolved }
    val resolvedCount = allItems.count { it.isResolved }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Green)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FindBack BD",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Green
                    )
                    Text(
                        text = if (currentUser != null) "Welcome, ${currentUser?.name}" else "Community Lost & Found",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                IconButton(onClick = onNotifs) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(containerColor = Danger, contentColor = Color.White) {
                                    Text(unreadNotifCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onSearch() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray)
                Spacer(Modifier.width(10.dp))
                Text("Search lost or found items, locations...", color = TextGray, fontSize = 14.sp)
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Green, GreenDark)))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lost or Found Something?",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$lostCount Lost • $foundCount Found • $resolvedCount Recovered",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onCreate,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Green
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Report Now", fontWeight = FontWeight.Bold)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryCard(
                        name = "All",
                        selected = activeCategory == "All",
                        onClick = { activeCategory = "All" }
                    )
                    SampleData.categories.forEach { cat ->
                        CategoryCard(
                            name = cat,
                            selected = activeCategory == cat,
                            onClick = {
                                activeCategory = if (activeCategory == cat) "All" else cat
                            }
                        )
                    }
                }
            }
        }
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (activeCategory == "All") "Recent Reports (${filteredItems.size})" else "$activeCategory Reports (${filteredItems.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            vm.setSelectedCategory(activeCategory)
                            onSearch()
                        }
                    ) {
                        Text("Search & Filter", color = Green)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Lost", "Found", "Resolved").forEach { status ->
                        FilterChip(
                            selected = activeTab == status,
                            onClick = { activeTab = status },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Green,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
        if (filteredItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reports match this filter yet. Tap 'Report Now' to post one!",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredItems, key = { it.id }) { item ->
                ItemCard(item = item, onClick = { onItem(item.id) })
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}
