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
fun AdminDashboardScreen(onReports: () -> Unit, onUsers: () -> Unit, onReported: () -> Unit, onStats: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AdminPanelSettings, null, tint = Green); Spacer(Modifier.width(8.dp))
            Text("Admin Panel", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onLogout) { Icon(Icons.AutoMirrored.Filled.Logout, null) }
        }
        AdminCard(Icons.Outlined.Description, "Manage Reports", "View and moderate all reports", onReports)
        AdminCard(Icons.Outlined.People, "Manage Users", "View and manage users", onUsers)
        AdminCard(Icons.Outlined.Report, "Reported Content", "Handle inappropriate content", onReported)
        AdminCard(Icons.Outlined.BarChart, "Analytics", "View platform statistics", onStats)
    }
}

@Composable
private fun AdminCard(icon: ImageVector, title: String, sub: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(GreenLight, RoundedCornerShape(12.dp)), Alignment.Center) { Icon(icon, null, tint = Green) }
            Spacer(Modifier.width(14.dp))
            Column { Text(title, fontWeight = FontWeight.SemiBold); Text(sub, fontSize = 12.sp, color = TextGray) }
        }
    }
}
