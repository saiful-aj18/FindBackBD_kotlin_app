package com.saiful.findbackbd.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun AdminDashboardScreen(
    onReports: () -> Unit,
    onUsers: () -> Unit,
    onReported: () -> Unit,
    onStats: () -> Unit,
    onLogout: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val items by vm.items.collectAsState()
    val users by vm.allUsers.collectAsState()
    val flagged by vm.flaggedReports.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Green)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Admin Panel",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            }
        }
        AdminCard(
            icon = Icons.Default.Description,
            title = "Manage Reports (${items.size})",
            sub = "${items.count { it.isLost && !it.isResolved }} Lost • ${items.count { !it.isLost && !it.isResolved }} Found • ${items.count { it.isResolved }} Resolved",
            onClick = onReports
        )
        AdminCard(
            icon = Icons.Default.Group,
            title = "Manage Users (${users.size})",
            sub = "${users.count { !it.isBlocked }} Active • ${users.count { it.isBlocked }} Blocked",
            onClick = onUsers
        )
        AdminCard(
            icon = Icons.Default.Flag,
            title = "Reported Content (${flagged.size})",
            sub = "Review and moderate flagged posts",
            onClick = onReported
        )
        AdminCard(
            icon = Icons.Default.Analytics,
            title = "Platform Analytics",
            sub = "View live recovery & match statistics",
            onClick = onStats
        )
    }
}

@Composable
fun AdminCard(
    icon: ImageVector,
    title: String,
    sub: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Green)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(sub, color = TextGray, fontSize = 13.sp)
            }
        }
    }
}
