package com.saiful.findbackbd.ui.navigation

import android.content.Intent
import android.net.Uri
import com.saiful.findbackbd.ui.screens.map.MapScreen
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
import com.saiful.findbackbd.ui.screens.admin.AdminDashboardScreen
import com.saiful.findbackbd.ui.screens.admin.AdminStatsScreen
import com.saiful.findbackbd.ui.screens.admin.ManageReportsScreen
import com.saiful.findbackbd.ui.screens.admin.ManageUsersScreen
import com.saiful.findbackbd.ui.screens.admin.ReportedContentScreen
import com.saiful.findbackbd.ui.screens.auth.*
import com.saiful.findbackbd.ui.screens.chat.ChatListScreen
import com.saiful.findbackbd.ui.screens.chat.ChatScreen
import com.saiful.findbackbd.ui.screens.details.ItemDetailsScreen
import com.saiful.findbackbd.ui.screens.home.HomeScreen
import com.saiful.findbackbd.ui.screens.notification.NotificationScreen
import com.saiful.findbackbd.ui.screens.onboarding.OnboardingScreen
import com.saiful.findbackbd.ui.screens.profile.ProfileScreen
import com.saiful.findbackbd.ui.screens.report.CreateReportScreen

import com.saiful.findbackbd.ui.screens.settings.SettingsScreen
import com.saiful.findbackbd.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val auth: AuthViewModel = viewModel()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route

    fun tab(r: String, home: String) = nav.navigate(r) {
        popUpTo(home) { saveState = true }; launchSingleTop = true; restoreState = true
    }
    val logout = {
        auth.reset()
        nav.navigate(Routes.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    Scaffold(bottomBar = {
        when {
            route in userBarRoutes -> AppBottomBar(userTabs, route) {
                if (it == Routes.CREATE) nav.navigate(it) else tab(it, Routes.HOME)
            }
            route in adminBarRoutes -> AppBottomBar(adminTabs, route) { tab(it, Routes.ADMIN_HOME) }
            else -> {}
        }
    }) { pad ->
        NavHost(nav, Routes.SPLASH, Modifier.padding(pad).then(if (route == Routes.SPLASH) Modifier else Modifier.statusBarsPadding())) {
            composable(Routes.SPLASH) { SplashScreen { nav.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } } }
            composable(Routes.ONBOARDING) { OnboardingScreen { nav.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } } } }
            composable(Routes.LOGIN) {
                LoginScreen(auth, onLoggedIn = { role ->
                    nav.navigate(if (role == Role.ADMIN) Routes.ADMIN_HOME else Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }, onRegister = { nav.navigate(Routes.REGISTER) })
            }
            composable(Routes.REGISTER) {
                RegisterScreen(onBack = { nav.navigateUp() }, onDone = { nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } })
            }
            // ---------- USER ----------
            composable(Routes.HOME) {
                HomeScreen(onItem = { nav.navigate("details/$it") }, onSearch = { tab(Routes.SEARCH, Routes.HOME) },
                    onCreate = { nav.navigate(Routes.CREATE) }, onNotifs = { nav.navigate(Routes.NOTIFS) })
            }

            composable(Routes.MAP) {
                MapScreen()
            }
            composable(Routes.CREATE) { CreateReportScreen(onBack = { nav.navigateUp() }, onSubmit = { nav.navigateUp() }) }
            composable(Routes.DETAILS, arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                ItemDetailsScreen(it.arguments?.getString("id") ?: "1", onBack = { nav.navigateUp() },
                    onMessage = { n -> nav.navigate("chat/${Uri.encode(n)}") })
            }
            composable(Routes.CHATS) { ChatListScreen { nav.navigate("chat/${Uri.encode(it)}") } }
            composable(Routes.CHAT, arguments = listOf(navArgument("name") { type = NavType.StringType })) {
                ChatScreen(it.arguments?.getString("name") ?: "", onBack = { nav.navigateUp() })
            }
            composable(Routes.NOTIFS) { NotificationScreen(onBack = { nav.navigateUp() }) }
            composable(Routes.PROFILE) {
                ProfileScreen(onSettings = { nav.navigate(Routes.SETTINGS) }, onMessages = { tab(Routes.CHATS, Routes.HOME) }, onLogout = { logout() })
            }
            composable(Routes.SETTINGS) { SettingsScreen(onBack = { nav.navigateUp() }, onLogout = { logout() }) }
            // ---------- ADMIN ----------
            composable(Routes.ADMIN_HOME) {
                AdminDashboardScreen(onReports = { tab(Routes.ADMIN_REPORTS, Routes.ADMIN_HOME) }, onUsers = { tab(Routes.ADMIN_USERS, Routes.ADMIN_HOME) },
                    onReported = { nav.navigate(Routes.ADMIN_REPORTED) }, onStats = { nav.navigate(Routes.ADMIN_STATS) }, onLogout = { logout() })
            }
            composable(Routes.ADMIN_REPORTS) { ManageReportsScreen { nav.navigateUp() } }
            composable(Routes.ADMIN_USERS) { ManageUsersScreen { nav.navigateUp() } }
            composable(Routes.ADMIN_REPORTED) { ReportedContentScreen { nav.navigateUp() } }

            composable(Routes.ADMIN_STATS) { AdminStatsScreen { nav.navigateUp() } }
            composable(Routes.ADMIN_SETTINGS) { SettingsScreen(onBack = { nav.navigateUp() }, onLogout = { logout() }) }
        }
    }
}


