package com.saiful.findbackbd.ui.screens.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.StatusChip
import com.saiful.findbackbd.ui.components.categoryIcon
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray
import com.saiful.findbackbd.utils.MatchUtils

@Composable
fun ItemDetailsScreen(
    id: String,
    onBack: () -> Unit,
    onMessage: (String) -> Unit,
    onSelectMatch: (String) -> Unit = {},
    vm: HomeViewModel = hiltViewModel()
) {
    val allItems by vm.items.collectAsState()
    val currentUser by vm.currentUser.collectAsState()
    val item = allItems.find { it.id == id } ?: SampleData.items.find { it.id == id } ?: allItems.firstOrNull()
    val ctx = LocalContext.current

    var showFlagDialog by remember { mutableStateOf(false) }
    var flagReason by remember { mutableStateOf("") }
    var flagSubmitted by remember { mutableStateOf(false) }

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Report not found", color = TextGray)
        }
        return
    }

    val potentialMatches = remember(allItems, item) {
        allItems
            .filter { it.id != item.id && it.isLost != item.isLost && !it.isResolved }
            .map { candidate -> candidate to MatchUtils.score(item, candidate) }
            .filter { (_, score) -> score >= 40 }
            .sortedByDescending { (_, score) -> score }
    }

    val isOwnerOrAdmin = currentUser?.id == item.ownerId ||
        currentUser?.name?.equals(item.contact, ignoreCase = true) == true ||
        currentUser?.role == Role.ADMIN

    if (showFlagDialog) {
        AlertDialog(
            onDismissRequest = { showFlagDialog = false },
            title = { Text("Report Inappropriate Post", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Why are you reporting \"${item.name}\"?", fontSize = 13.sp, color = TextGray)
                    listOf(
                        "Suspected spam or fake report",
                        "Duplicate post",
                        "Caller asked for money / reward",
                        "Incorrect location or contact info"
                    ).forEach { preset ->
                        OutlinedButton(
                            onClick = { flagReason = preset },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(preset, fontSize = 12.sp, color = if (flagReason == preset) Green else TextGray)
                        }
                    }
                    AppTextField(
                        value = flagReason,
                        onChange = { flagReason = it },
                        hint = "Or write custom reason..."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.flagItem(item, flagReason.ifBlank { "Flagged by community user" })
                        showFlagDialog = false
                        flagSubmitted = true
                    }
                ) {
                    Text("Submit Flag", color = Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFlagDialog = false }) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(GreenLight)
        ) {
            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 60f
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            color = Color(0xFFC8E8D8),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 2f
                        )
                        x += step
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = Color(0xFFC8E8D8),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2f
                        )
                        y += step
                    }
                }
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Map Pin",
                        tint = if (item.isLost) Danger else Green,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = item.place,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF121816)
                    )
                    Text(
                        text = "${item.latitude}, ${item.longitude}",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.TopStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            IconButton(
                onClick = { showFlagDialog = true },
                modifier = Modifier
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Flag, contentDescription = "Flag Report", tint = Danger)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = categoryIcon(item.category),
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Category: ${item.category}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    StatusChip(lost = item.isLost, isResolved = item.isResolved)
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("${item.place} (${item.distance})", color = TextGray, fontSize = 13.sp)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("${item.date} • ${item.time}", color = TextGray, fontSize = 13.sp)
                }
                Spacer(Modifier.height(14.dp))
                Text(item.description, fontSize = 14.sp)

                if (flagSubmitted) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Flag submitted to moderators for review.",
                        color = Green,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(18.dp))
                Text("Reported By", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.contact.ifBlank { "U" }.take(1).uppercase(),
                            color = Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.contact.ifBlank { "Community Member" }, fontWeight = FontWeight.SemiBold)
                        Text(item.contactPhone.ifBlank { "+880 1712 345678" }, color = TextGray, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppButton(
                        text = "Message",
                        onClick = {
                            vm.startChat(
                                participantName = item.contact.ifBlank { "Community Member" },
                                participantPhone = item.contactPhone,
                                itemId = item.id,
                                itemName = item.name
                            ) { thread ->
                                onMessage(thread.participantName)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Call",
                        onClick = {
                            val cleanPhone = item.contactPhone.ifBlank { "+8801712345678" }.replace(" ", "")
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
                            ctx.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        outlined = true
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { vm.markResolved(item.id, !item.isResolved) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Green,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (item.isResolved) "Reopen Report" else "Mark Resolved",
                            color = Green,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (isOwnerOrAdmin) {
                        OutlinedButton(
                            onClick = {
                                vm.deleteItem(item.id)
                                onBack()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Report",
                                tint = Danger,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        if (potentialMatches.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenLight.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Green)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Smart Potential Matches (${potentialMatches.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Green
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    potentialMatches.forEach { (matchItem, score) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { onSelectMatch(matchItem.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(matchItem.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "${if (matchItem.isLost) "Lost" else "Found"} • ${matchItem.place} • By ${matchItem.contact}",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Green)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("$score% Match", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
