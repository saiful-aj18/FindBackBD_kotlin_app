package com.saiful.findbackbd.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.Segmented
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onLoggedIn: (Role) -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit = {}
) {
    val ui by vm.ui.collectAsState()
    val isAdmin = ui.role == Role.ADMIN

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Green),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text("FindBack BD", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Green)
        Text("Lost & Found, Together", color = TextGray, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))

        Segmented(
            options = listOf("User Login", "Admin Login"),
            selected = if (isAdmin) 1 else 0,
            onSelect = { idx -> vm.setRole(if (idx == 1) Role.ADMIN else Role.USER) }
        )
        Spacer(Modifier.height(16.dp))

        if (isAdmin) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenLight)
                    .clickable {
                        vm.onEmail("admin@findback.bd")
                        vm.onPassword("admin123")
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Green)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Restricted area. Authorized administrators only.",
                        fontSize = 12.sp,
                        color = Green,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Tap to auto-fill Admin: admin@findback.bd / admin123",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = GreenLight.copy(alpha = 0.65f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Green, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Sign in with your email/phone or register a new account.",
                            fontSize = 12.sp,
                            color = Green
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        AppTextField(
            value = ui.email,
            onChange = vm::onEmail,
            hint = if (isAdmin) "Admin email" else "Email or Phone",
            icon = Icons.Default.Email
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = ui.password,
            onChange = vm::onPassword,
            hint = "Password (min 6 chars)",
            icon = Icons.Default.Lock,
            password = true
        )

        if (!isAdmin) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = onForgot) {
                    Text("Forgot Password?", color = Green, fontSize = 13.sp)
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
        }

        if (ui.error != null) {
            Text(
                text = ui.error ?: "",
                color = Danger,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        AppButton(
            text = when {
                ui.isLoading -> "Signing in..."
                isAdmin -> "Login as Admin"
                else -> "Login"
            },
            enabled = !ui.isLoading,
            onClick = { vm.login(onLoggedIn) }
        )

        if (!isAdmin) {
            Spacer(Modifier.height(16.dp))
            Text("OR", color = TextGray, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            AppButton(
                text = "Continue with Google",
                onClick = { vm.loginWithGoogle(onLoggedIn) },
                outlined = true,
                enabled = !ui.isLoading
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = TextGray, fontSize = 14.sp)
                TextButton(onClick = onRegister) {
                    Text("Register", color = Green, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
