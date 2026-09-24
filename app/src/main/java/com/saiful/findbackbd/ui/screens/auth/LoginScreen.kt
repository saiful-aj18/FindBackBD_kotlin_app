package com.saiful.findbackbd.ui.screens.auth

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
fun LoginScreen(vm: AuthViewModel, onLoggedIn: (Role) -> Unit, onRegister: () -> Unit) {
    val s by vm.ui.collectAsState()
    val isAdmin = s.role == Role.ADMIN
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Outlined.LocationOn, null, Modifier.size(56.dp), tint = Green)
        Text("FindBack BD", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Green)
        Text("Lost & Found, Together", color = TextGray, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        // Role selector: decides which login flow is used
        Segmented(listOf("User Login", "Admin Login"), if (isAdmin) 1 else 0) { vm.setRole(if (it == 1) Role.ADMIN else Role.USER) }
        Spacer(Modifier.height(20.dp))
        if (isAdmin) {
            Row(Modifier.fillMaxWidth().background(GreenLight, RoundedCornerShape(10.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AdminPanelSettings, null, tint = Green); Spacer(Modifier.width(8.dp))
                Text("Restricted area. Authorized administrators only.", fontSize = 12.sp)
            }
            Spacer(Modifier.height(12.dp))
        }
        AppTextField(s.email, vm::onEmail, if (isAdmin) "Admin email" else "Email or Phone", if (isAdmin) Icons.Outlined.AdminPanelSettings else Icons.Outlined.Person)
        Spacer(Modifier.height(12.dp))
        AppTextField(s.password, vm::onPassword, "Password", Icons.Outlined.Lock, password = true)
        if (!isAdmin) TextButton({}, Modifier.align(Alignment.End)) { Text("Forgot Password?", fontSize = 12.sp) } else Spacer(Modifier.height(8.dp))
        s.error?.let { Text(it, color = Danger, fontSize = 13.sp); Spacer(Modifier.height(8.dp)) }
        AppButton(if (isAdmin) "Login as Admin" else "Login", { vm.login(onLoggedIn) })
        if (!isAdmin) { // admins cannot self-register or use social login
            Spacer(Modifier.height(12.dp)); Text("OR", color = TextGray, fontSize = 12.sp); Spacer(Modifier.height(12.dp))
            AppButton("Continue with Google", { /* TODO Google Sign-In */ }, outlined = true)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", fontSize = 13.sp, color = TextGray)
                TextButton(onRegister) { Text("Register") }
            }
        }
    }
}
