package com.saiful.findbackbd.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import com.saiful.findbackbd.ui.components.NavItem

object Routes {
    const val MAP = "map"
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val HOME = "home"
    const val SEARCH = "search"
    const val CREATE = "create"
    const val CHATS = "chats"
    const val PROFILE = "profile"
    const val DETAILS = "details/{id}"
    const val CHAT = "chat/{name}"
    const val NOTIFS = "notifications"
    const val SETTINGS = "settings"
    const val ADMIN_HOME = "admin_home"
    const val ADMIN_REPORTS = "admin_reports"
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_REPORTED = "admin_reported"
    const val ADMIN_STATS = "admin_stats"
    const val ADMIN_SETTINGS = "admin_settings"
}

val userTabs = listOf(
    NavItem(Routes.HOME, "Home", Icons.Default.Home),
    NavItem(Routes.SEARCH, "Search", Icons.Default.Search),
    NavItem(Routes.CREATE, "Create", Icons.Default.AddCircle),
    NavItem(Routes.CHATS, "Chat", Icons.AutoMirrored.Filled.Chat),
    NavItem(Routes.PROFILE, "Profile", Icons.Default.Person)
)

val adminTabs = listOf(
    NavItem(Routes.ADMIN_HOME, "Home", Icons.Default.Dashboard),
    NavItem(Routes.ADMIN_REPORTS, "Reports", Icons.Default.Assessment),
    NavItem(Routes.ADMIN_USERS, "Users", Icons.Default.Group),
    NavItem(Routes.ADMIN_SETTINGS, "Settings", Icons.Default.Settings)
)

val userBarRoutes = setOf(
    Routes.HOME,
    Routes.SEARCH,
    Routes.CHATS,
    Routes.PROFILE,
    Routes.NOTIFS,
    Routes.SETTINGS
)

val adminBarRoutes = setOf(
    Routes.ADMIN_HOME,
    Routes.ADMIN_REPORTS,
    Routes.ADMIN_USERS,
    Routes.ADMIN_REPORTED,
    Routes.ADMIN_STATS,
    Routes.ADMIN_SETTINGS
)
