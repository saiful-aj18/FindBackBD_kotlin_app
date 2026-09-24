package com.saiful.findbackbd.ui.screens.settings

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
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var push by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackBar("Settings", onBack)
        SectionTitle("Account")
        MenuRow(Icons.Outlined.Edit, "Edit Profile"); MenuRow(Icons.Outlined.Lock, "Change Password")
        SectionTitle("Preferences")
        SwitchRow(Icons.Outlined.DarkMode, "Dark Mode", AppSettings.dark) { AppSettings.dark = it }
        MenuRow(Icons.Outlined.Language, "Language", trailing = "English")
        SectionTitle("Notifications")
        SwitchRow(Icons.Outlined.Notifications, "Push Notifications", push) { push = it }
        SectionTitle("About")
        MenuRow(Icons.Outlined.HelpOutline, "Help & Support"); MenuRow(Icons.Outlined.Info, "About FindBack BD")
        Box(Modifier.padding(16.dp)) { AppButton("Logout", onLogout, outlined = true) }
    }
}
