package com.saiful.findbackbd.ui.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.activity.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import com.saiful.findbackbd.data.model.*
import com.saiful.findbackbd.ui.theme.*
import com.saiful.findbackbd.ui.components.*
import com.saiful.findbackbd.ui.navigation.*
import com.saiful.findbackbd.ui.screens.auth.*
import com.saiful.findbackbd.ui.screens.admin.*
import com.saiful.findbackbd.ui.screens.home.*
import com.saiful.findbackbd.ui.screens.search.*
import com.saiful.findbackbd.ui.screens.report.*
import com.saiful.findbackbd.ui.screens.details.*
import com.saiful.findbackbd.ui.screens.chat.*
import com.saiful.findbackbd.ui.screens.notification.*
import com.saiful.findbackbd.ui.screens.profile.*
import com.saiful.findbackbd.ui.screens.settings.*
import com.saiful.findbackbd.ui.screens.splash.*
import com.saiful.findbackbd.ui.screens.onboarding.*

@Composable
fun ChatScreen(name: String, onBack: () -> Unit) {
    val msgs = remember { mutableStateListOf<Message>().apply { addAll(SampleData.messages) } }
    var text by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().imePadding()) {
        BackBar(name, onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(msgs) { m ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start) {
                    Column(Modifier.widthIn(max = 260.dp).background(if (m.mine) Green else GreenLight, RoundedCornerShape(14.dp)).padding(10.dp)) {
                        Text(m.text, color = if (m.mine) Color.White else Color.Black)
                        Text(m.time, fontSize = 10.sp, color = if (m.mine) Color.White.copy(alpha = .7f) else TextGray)
                    }
                }
            }
        }
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(text, { text = it }, Modifier.weight(1f), placeholder = { Text("Type a message...") }, shape = RoundedCornerShape(24.dp))
            Spacer(Modifier.width(8.dp))
            // TODO: ChatViewModel -> Firestore chats/{chatId}/messages (real-time)
            IconButton({ if (text.isNotBlank()) { msgs.add(Message(text.trim(), "now", true)); text = "" } }, Modifier.background(Green, CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
            }
        }
    }
}
