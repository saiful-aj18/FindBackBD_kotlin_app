package com.saiful.findbackbd.ui.screens.chat

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun ChatListScreen(
    onChat: (String) -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val threads by vm.chatThreads.collectAsState()
    val allUsers by vm.allUsers.collectAsState()
    val currentUser by vm.currentUser.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredThreads = remember(threads, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) threads
        else threads.filter {
            it.participantName.lowercase().contains(q) ||
                it.itemName.lowercase().contains(q) ||
                it.lastMessage.lowercase().contains(q)
        }
    }

    val communityContacts = remember(allUsers, currentUser) {
        allUsers.filter { it.id != currentUser?.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Messages",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search conversations or items...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Green) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Green,
                unfocusedBorderColor = Color(0xFFDCE5E1),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Text(
            text = "Quick Connect Community",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextGray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            communityContacts.forEach { user ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        vm.startChat(
                            participantName = user.name,
                            participantPhone = user.phone
                        ) { thread ->
                            onChat(thread.participantName)
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            color = Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = user.name.substringBefore(" "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        HorizontalDivider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredThreads, key = { it.id }) { thread ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChat(thread.participantName) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thread.participantName.take(1).uppercase(),
                            color = Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = thread.participantName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = thread.lastTime,
                                color = if (thread.unreadCount > 0) Green else TextGray,
                                fontSize = 11.sp,
                                fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (thread.itemName.isNotBlank()) {
                            Text(
                                text = "Re: ${thread.itemName}",
                                color = Green,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = thread.lastMessage,
                                color = TextGray,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (thread.unreadCount > 0) {
                                Spacer(Modifier.width(8.dp))
                                Badge(containerColor = Green, contentColor = Color.White) {
                                    Text(thread.unreadCount.toString())
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
