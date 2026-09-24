package com.saiful.findbackbd.ui.screens.admin

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
import androidx.compose.ui.graphics.Color.Companion.Green
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
fun ManageUsersScreen(onBack: () -> Unit) {
    val blocked = remember { mutableStateMapOf<String, Boolean>() }
    Column(Modifier.fillMaxSize()) {
        BackBar("Manage Users", onBack)
        SampleData.users.forEach { u ->
            Row(Modifier.fillMaxWidth().padding(16.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).background(GreenLight, CircleShape), Alignment.Center) { Text(u.name.take(1), color = Green, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { Text(u.name, fontWeight = FontWeight.Medium); Text(u.email, fontSize = 12.sp, color = TextGray) }
                Text(if (blocked[u.id] == true) "Blocked" else "Active", fontSize = 12.sp, color = if (blocked[u.id] == true) Danger else Green)
                Spacer(Modifier.width(8.dp))
                Switch(blocked[u.id] != true, { blocked[u.id] = !it })
            }
        }
    }
}
