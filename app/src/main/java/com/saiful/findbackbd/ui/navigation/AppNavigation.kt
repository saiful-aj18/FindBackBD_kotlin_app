package com.saiful.findbackbd.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.ui.components.AppBottomBar
import com.saiful.findbackbd.ui.screens.admin.AdminDashboardScreen
import com.saiful.findbackbd.ui.screens.admin.AdminStatsScreen
import com.saiful.findbackbd.ui.screens.admin.ManageReportsScreen
import com.saiful.findbackbd.ui.screens.admin.ManageUsersScreen
import com.saiful.findbackbd.ui.screens.admin.ReportedContentScreen
import com.saiful.findbackbd.ui.screens.auth.AuthViewModel
import com.saiful.findbackbd.ui.screens.auth.ForgotPasswordScreen
import com.saiful.findbackbd.ui.screens.auth.LoginScreen
import com.saiful.findbackbd.ui.screens.auth.RegisterScreen
import com.saiful.findbackbd.ui.screens.chat.ChatListScreen
import com.saiful.findbackbd.ui.screens.chat.ChatScreen
import com.saiful.findbackbd.ui.screens.details.ItemDetailsScreen
import com.saiful.findbackbd.ui.screens.home.HomeScreen
import com.saiful.findbackbd.ui.screens.home.SearchScreen
import com.saiful.findbackbd.ui.screens.map.MapScreen
import com.saiful.findbackbd.ui.screens.notification.NotificationScreen
import com.saiful.findbackbd.ui.screens.onboarding.OnboardingScreen
import com.saiful.findbackbd.ui.screens.profile.ProfileScreen
import com.saiful.findbackbd.ui.screens.report.CreateReportScreen
import com.saiful.findbackbd.ui.screens.settings.SettingsScreen
import com.saiful.findbackbd.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    val authVm: AuthViewModel = hiltViewModel()

    val doLogout: () -> Unit = {
        authVm.reset()
        nav.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            if (current in userBarRoutes) {
                AppBottomBar(userTabs, current) { route ->
                    if (route == Routes.CREATE) {
                        nav.navigate(Routes.CREATE)
                    } else {
                        nav.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            } else if (current in adminBarRoutes) {
                AppBottomBar(adminTabs, current) { route ->
                    nav.navigate(route) {
                        popUpTo(Routes.ADMIN_HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(pad)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        val existingUser = authVm.currentUser.value
                        val nextRoute = when {
                            existingUser?.role == Role.ADMIN -> Routes.ADMIN_HOME
                            existingUser != null -> Routes.HOME
                            else -> Routes.ONBOARDING
                        }
                        nav.navigate(nextRoute) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onDone = {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    vm = authVm,
                    onLoggedIn = { role ->
                        val dest = if (role == Role.ADMIN) Routes.ADMIN_HOME else Routes.HOME
                        nav.navigate(dest) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onRegister = { nav.navigate(Routes.REGISTER) },
                    onForgot = { nav.navigate(Routes.FORGOT) }
                )
            }
            composable(Routes.FORGOT) {
                ForgotPasswordScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBack = { nav.popBackStack() },
                    onDone = {
                        val role = authVm.currentUser.value?.role ?: Role.USER
                        val dest = if (role == Role.ADMIN) Routes.ADMIN_HOME else Routes.HOME
                        nav.navigate(dest) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onItem = { id -> nav.navigate("details/${Uri.encode(id)}") },
                    onSearch = { nav.navigate(Routes.SEARCH) },
                    onCreate = { nav.navigate(Routes.CREATE) },
                    onNotifs = { nav.navigate(Routes.NOTIFS) }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onCreate = { nav.navigate(Routes.CREATE) },
                    onDetails = { id -> nav.navigate("details/${Uri.encode(id)}") },
                    onMap = { nav.navigate(Routes.MAP) }
                )
            }
            composable(Routes.MAP) {
                MapScreen(
                    onBack = { nav.popBackStack() },
                    onDetails = { id -> nav.navigate("details/${Uri.encode(id)}") }
                )
            }
            composable(Routes.CREATE) {
                CreateReportScreen(
                    onBack = { nav.popBackStack() },
                    onSubmit = { nav.popBackStack() }
                )
            }
            composable(
                route = Routes.DETAILS,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: "item_1"
                ItemDetailsScreen(
                    id = id,
                    onBack = { nav.popBackStack() },
                    onMessage = { name -> nav.navigate("chat/${Uri.encode(name)}") },
                    onSelectMatch = { matchId -> nav.navigate("details/${Uri.encode(matchId)}") }
                )
            }
            composable(Routes.CHATS) {
                ChatListScreen(
                    onChat = { name -> nav.navigate("chat/${Uri.encode(name)}") }
                )
            }
            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { entry ->
                val name = entry.arguments?.getString("name") ?: "User"
                ChatScreen(name = name, onBack = { nav.popBackStack() })
            }
            composable(Routes.NOTIFS) {
                NotificationScreen(
                    onBack = { nav.popBackStack() },
                    onOpenItem = { itemId -> nav.navigate("details/${Uri.encode(itemId)}") }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onSettings = { nav.navigate(Routes.SETTINGS) },
                    onMessages = { nav.navigate(Routes.CHATS) },
                    onLogout = doLogout,
                    onItemDetails = { id -> nav.navigate("details/${Uri.encode(id)}") },
                    onCreateReport = { nav.navigate(Routes.CREATE) }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { nav.popBackStack() },
                    onLogout = doLogout
                )
            }
            composable(Routes.ADMIN_HOME) {
                AdminDashboardScreen(
                    onReports = { nav.navigate(Routes.ADMIN_REPORTS) },
                    onUsers = { nav.navigate(Routes.ADMIN_USERS) },
                    onReported = { nav.navigate(Routes.ADMIN_REPORTED) },
                    onStats = { nav.navigate(Routes.ADMIN_STATS) },
                    onLogout = doLogout
                )
            }
            composable(Routes.ADMIN_REPORTS) {
                ManageReportsScreen(
                    onBack = { nav.popBackStack() },
                    onDetails = { id -> nav.navigate("details/${Uri.encode(id)}") }
                )
            }
            composable(Routes.ADMIN_USERS) {
                ManageUsersScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.ADMIN_REPORTED) {
                ReportedContentScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.ADMIN_STATS) {
                AdminStatsScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.ADMIN_SETTINGS) {
                SettingsScreen(
                    onBack = { nav.popBackStack() },
                    onLogout = doLogout
                )
            }
        }
    }
}
