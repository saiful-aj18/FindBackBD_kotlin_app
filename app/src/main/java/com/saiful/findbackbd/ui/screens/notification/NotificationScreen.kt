package com.saiful.findbackbd.ui.screens.notification

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
fun NotificationScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        BackBar("Notifications", onBack)
        LazyColumn {
            items(SampleData.notifications) { n ->
                val (icon, color) = when (n.type) {
                    "match" -> Icons.Outlined.FavoriteBorder to Danger
                    "message" -> Icons.Outlined.ChatBubbleOutline to Color(0xFF3B82F6)
                    "status" -> Icons.Outlined.CheckCircleOutline to Green
                    else -> Icons.Outlined.LocationOn to Green
                }
                Row(Modifier.fillMaxWidth().padding(16.dp, 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).background(color.copy(alpha = .15f), CircleShape), Alignment.Center) { Icon(icon, null, tint = color) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(n.title, fontWeight = FontWeight.Medium); Text(n.body, fontSize = 12.sp, color = TextGray) }
                    Text(n.time, fontSize = 11.sp, color = TextGray)
                }
            }
        }
    }
}
