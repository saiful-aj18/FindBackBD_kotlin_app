package com.saiful.findbackbd.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.saiful.findbackbd.data.model.*
import com.saiful.findbackbd.ui.theme.*
import com.saiful.findbackbd.ui.components.*

@Composable
fun HomeScreen(onItem: (String) -> Unit, onSearch: () -> Unit, onCreate: () -> Unit, onNotifs: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = Green)
                Text("FindBack BD", fontWeight = FontWeight.Bold, color = Green, fontSize = 18.sp, modifier = Modifier.weight(1f))
                IconButton(onNotifs) { Icon(Icons.Outlined.Notifications, null) }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().border(1.dp, Color(0xFFDDE5E1), RoundedCornerShape(12.dp)).clickable(onClick = onSearch).padding(14.dp)) {
                Icon(Icons.Outlined.Search, null, tint = TextGray); Spacer(Modifier.width(8.dp))
                Text("Search items, categories...", color = TextGray, fontSize = 13.sp)
            }
        }
        item {
            Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Green, GreenDark)), RoundedCornerShape(16.dp)).padding(18.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Lost Something?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Report it and get help", color = Color.White.copy(alpha = .85f), fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(onCreate, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Green)) { Text("Report Now") }
                }
                Icon(Icons.Outlined.Description, null, Modifier.size(64.dp), tint = Color.White.copy(alpha = .6f))
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SampleData.categories.chunked(4).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { row.forEach { CategoryCard(it, onSearch) } }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recent Reports", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                TextButton(onSearch) { Text("View All") }
            }
        }
        items(SampleData.items) { ItemCard(it) { onItem(it.id) } }
    }
}
