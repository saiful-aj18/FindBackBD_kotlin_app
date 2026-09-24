package com.saiful.findbackbd.ui.navigation

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

object Routes {
    const val MAP = "map"
    const val SPLASH = "splash"; const val ONBOARDING = "onboarding"; const val LOGIN = "login"; const val REGISTER = "register"
    const val HOME = "home"; const val SEARCH = "search"; const val CREATE = "create"; const val CHATS = "chats"; const val PROFILE = "profile"
    const val DETAILS = "details/{id}"; const val CHAT = "chat/{name}"; const val NOTIFS = "notifications"; const val SETTINGS = "settings"
    const val ADMIN_HOME = "admin_home"; const val ADMIN_REPORTS = "admin_reports"; const val ADMIN_USERS = "admin_users"
    const val ADMIN_REPORTED = "admin_reported"; const val ADMIN_STATS = "admin_stats"; const val ADMIN_SETTINGS = "admin_settings"
}

val userTabs = listOf(
    NavItem(Routes.HOME, "Home", Icons.Outlined.Home), NavItem(Routes.SEARCH, "Search", Icons.Outlined.Search),
    NavItem(Routes.CREATE, "Create", Icons.Outlined.AddCircle), NavItem(Routes.CHATS, "Chat", Icons.Outlined.ChatBubbleOutline),
    NavItem(Routes.PROFILE, "Profile", Icons.Outlined.Person)
)
val adminTabs = listOf(
    NavItem(Routes.ADMIN_HOME, "Home", Icons.Outlined.Home), NavItem(Routes.ADMIN_REPORTS, "Reports", Icons.Outlined.Description),
    NavItem(Routes.ADMIN_USERS, "Users", Icons.Outlined.People), NavItem(Routes.ADMIN_SETTINGS, "Settings", Icons.Outlined.Settings)
)
val userBarRoutes = setOf(Routes.HOME, Routes.SEARCH, Routes.CHATS, Routes.PROFILE, Routes.NOTIFS, Routes.SETTINGS)
val adminBarRoutes = setOf(Routes.ADMIN_HOME, Routes.ADMIN_REPORTS, Routes.ADMIN_USERS, Routes.ADMIN_REPORTED, Routes.ADMIN_STATS, Routes.ADMIN_SETTINGS)
