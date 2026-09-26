package com.saiful.findbackbd.ui.screens.chat

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.data.model.ChatThread
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun ChatScreen(
    name: String,
    onBack: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current
    var activeThread by remember { mutableStateOf<ChatThread?>(null) }
    var text by remember { mutableStateOf("") }

    LaunchedEffect(name) {
        vm.startChat(participantName = name) { thread ->
            activeThread = thread
            vm.markChatRead(thread.id)
        }
    }

    val chatId = activeThread?.id ?: ("chat_" + name.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_'))
    val msgs by vm.messagesForChat(chatId).collectAsState(initial = emptyList())
    val listState = rememberLazyListState()

    LaunchedEffect(msgs.size) {
        if (msgs.isNotEmpty()) {
            listState.animateScrollToItem(msgs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackBar(
            title = name,
            onBack = onBack,
            trailing = {
                IconButton(
                    onClick = {
                        val phone = (activeThread?.participantPhone ?: "+8801712345678").replace(" ", "")
                        ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                    }
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call $name", tint = Green)
                }
            }
        )

        if (!activeThread?.itemName.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenLight)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Regarding Report: ${activeThread?.itemName}",
                    color = Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(msgs, key = { it.id }) { m ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (m.mine) Green else GreenLight)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        if (!m.mine && m.senderName.isNotBlank()) {
                            Text(
                                text = m.senderName,
                                color = Green,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = m.text,
                            color = if (m.mine) Color.White else Color(0xFF121816),
                            fontSize = 14.sp
                        )
                        Text(
                            text = m.time,
                            color = if (m.mine) Color.White.copy(alpha = 0.75f) else TextGray,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        // Quick-reply chips for fast coordination
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Where can we meet?",
                "Is this still available?",
                "Can I call you?",
                "Thank you!"
            ).forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreenLight)
                        .clickable {
                            vm.sendMessage(
                                chatId = chatId,
                                text = suggestion,
                                participantName = name,
                                participantPhone = activeThread?.participantPhone ?: "+880 1712 345678",
                                itemId = activeThread?.itemId ?: "",
                                itemName = activeThread?.itemName ?: ""
                            )
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(suggestion, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Type a message to $name...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val toSend = text.trim()
                    if (toSend.isNotBlank()) {
                        vm.sendMessage(
                            chatId = chatId,
                            text = toSend,
                            participantName = name,
                            participantPhone = activeThread?.participantPhone ?: "+880 1712 345678",
                            itemId = activeThread?.itemId ?: "",
                            itemName = activeThread?.itemName ?: ""
                        )
                        text = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Green)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}
