package com.saiful.findbackbd.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.ItemCard
import com.saiful.findbackbd.ui.components.MenuRow
import com.saiful.findbackbd.ui.screens.auth.AuthViewModel
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray
import com.saiful.findbackbd.utils.MatchUtils

@Composable
fun ProfileScreen(
    onSettings: () -> Unit,
    onMessages: () -> Unit,
    onLogout: () -> Unit,
    onItemDetails: (String) -> Unit = {},
    onCreateReport: () -> Unit = {},
    vm: HomeViewModel = hiltViewModel(),
    authVm: AuthViewModel = hiltViewModel()
) {
    val currentUser by vm.currentUser.collectAsState()
    val allItems by vm.items.collectAsState()
    val chatThreads by vm.chatThreads.collectAsState()

    val displayName = currentUser?.name?.ifBlank { "Community Member" } ?: "Community Member"
    val displayEmail = currentUser?.email?.ifBlank { "member@findback.bd" } ?: "member@findback.bd"
    val displayPhone = currentUser?.phone?.ifBlank { "+880 1712 345678" } ?: "+880 1712 345678"
    val initials = remember(displayName) {
        displayName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
            .ifBlank { "U" }
    }

    val myReports = remember(allItems, currentUser) {
        val uid = currentUser?.id ?: ""
        val uname = currentUser?.name ?: ""
        allItems.filter { item ->
            (uid.isNotBlank() && item.ownerId == uid) ||
                (uname.isNotBlank() && item.contact.equals(uname, ignoreCase = true))
        }
    }

    val smartMatches = remember(allItems) {
        val lostList = allItems.filter { it.isLost && !it.isResolved }
        val foundList = allItems.filter { !it.isLost && !it.isResolved }
        val pairs = mutableListOf<Triple<String, String, Int>>()
        for (lost in lostList) {
            for (found in foundList) {
                val score = MatchUtils.score(lost, found)
                if (score >= 40) {
                    pairs.add(Triple(found.id, "${lost.name} ↔ ${found.name} (${found.place})", score))
                }
            }
        }
        pairs.sortedByDescending { it.third }
    }

    val recoveredCount = allItems.count { it.isResolved }

    var activeSection by remember { mutableStateOf("my_reports") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(displayName) { mutableStateOf(displayName) }
    var editEmail by remember(displayEmail) { mutableStateOf(displayEmail) }
    var editPhone by remember(displayPhone) { mutableStateOf(displayPhone) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(
                        value = editName,
                        onChange = { editName = it },
                        hint = "Full Name",
                        icon = Icons.Default.Person
                    )
                    AppTextField(
                        value = editEmail,
                        onChange = { editEmail = it },
                        hint = "Email Address",
                        icon = Icons.Default.Email
                    )
                    AppTextField(
                        value = editPhone,
                        onChange = { editPhone = it },
                        hint = "Phone Number",
                        icon = Icons.Default.Phone
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authVm.updateProfile(editName, editEmail, editPhone) {
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Save Changes", color = Green, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Green)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = Green, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(displayName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        editName = displayName
                        editEmail = displayEmail
                        editPhone = displayPhone
                        showEditDialog = true
                    },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(displayEmail, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            Text(displayPhone, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val stats = listOf(
                    myReports.size.toString() to "My Reports",
                    smartMatches.size.toString() to "Matches",
                    recoveredCount.toString() to "Recovered"
                )
                stats.forEach { (num, label) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(num, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                }
            }
        }

        MenuRow(
            icon = Icons.Default.Edit,
            title = "Edit Account Details",
            trailing = "Update",
            onClick = {
                editName = displayName
                editEmail = displayEmail
                editPhone = displayPhone
                showEditDialog = true
            }
        )
        MenuRow(
            icon = Icons.Default.Description,
            title = "My Submitted Reports",
            trailing = "${myReports.size} posts",
            onClick = { activeSection = if (activeSection == "my_reports") "" else "my_reports" }
        )

        if (activeSection == "my_reports") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (myReports.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenLight.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCreateReport() }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Green)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "You haven't posted a report under $displayName yet. Tap to create one!",
                                fontSize = 13.sp,
                                color = Green,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    myReports.forEach { report ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.weight(1f)) {
                                ItemCard(item = report, onClick = { onItemDetails(report.id) })
                            }
                            IconButton(onClick = { vm.markResolved(report.id, !report.isResolved) }) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Toggle Resolved",
                                    tint = if (report.isResolved) Green else TextGray
                                )
                            }
                            IconButton(onClick = { vm.deleteItem(report.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Report",
                                    tint = Danger
                                )
                            }
                        }
                    }
                }
            }
        }

        MenuRow(
            icon = Icons.Default.AutoAwesome,
            title = "Smart Matches",
            trailing = "${smartMatches.size} found",
            onClick = { activeSection = if (activeSection == "matches") "" else "matches" }
        )

        if (activeSection == "matches") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (smartMatches.isEmpty()) {
                    Text(
                        text = "No high-confidence matches right now.",
                        color = TextGray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    smartMatches.forEach { (itemId, label, score) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemDetails(itemId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Green)
                                Spacer(Modifier.width(10.dp))
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                Text("$score%", color = Green, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        MenuRow(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "Messages",
            trailing = "${chatThreads.size} chats",
            onClick = onMessages
        )
        MenuRow(
            icon = Icons.Default.Settings,
            title = "Settings",
            onClick = onSettings
        )
        Box(modifier = Modifier.padding(16.dp)) {
            AppButton(text = "Logout", onClick = onLogout, outlined = true)
        }
    }
}
