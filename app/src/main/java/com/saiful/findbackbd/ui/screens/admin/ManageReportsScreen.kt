package com.saiful.findbackbd.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.components.ItemCard
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun ManageReportsScreen(
    onBack: () -> Unit,
    onDetails: (String) -> Unit = {},
    vm: HomeViewModel = hiltViewModel()
) {
    val list by vm.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackBar(title = "Manage Reports (${list.size})", onBack = onBack)
        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reports available.", color = TextGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(list, key = { it.id }) { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) {
                            ItemCard(item = item, onClick = { onDetails(item.id) })
                        }
                        IconButton(onClick = { vm.markResolved(item.id, !item.isResolved) }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Toggle Resolved",
                                tint = if (item.isResolved) Green else TextGray
                            )
                        }
                        IconButton(onClick = { vm.deleteItem(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Danger)
                        }
                    }
                }
            }
        }
    }
}
