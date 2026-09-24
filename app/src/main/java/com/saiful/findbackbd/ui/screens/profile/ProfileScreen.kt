package com.saiful.findbackbd.ui.screens.profile

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
fun ProfileScreen(onSettings: () -> Unit, onMessages: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(Modifier.fillMaxWidth().background(Green).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(84.dp).background(Color.White, CircleShape), Alignment.Center) { Text("RH", color = Green, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            Text("Rifat Hasan", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("rifat@example.com", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("12" to "Reports", "5" to "Matches", "4.8" to "Rating").forEach { (n, l) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(n, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text(l, color = Color.White.copy(alpha = .8f), fontSize = 12.sp) }
                }
            }
        }
        MenuRow(Icons.Outlined.Description, "My Reports")
        MenuRow(Icons.Outlined.FavoriteBorder, "My Matches")
        MenuRow(Icons.Outlined.ChatBubbleOutline, "Messages", onMessages)
        MenuRow(Icons.Outlined.StarBorder, "Reviews")
        MenuRow(Icons.Outlined.Settings, "Settings", onSettings)
        Box(Modifier.padding(16.dp)) { AppButton("Logout", onLogout, outlined = true) }
    }
}
