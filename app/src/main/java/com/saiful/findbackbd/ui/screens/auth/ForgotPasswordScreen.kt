package com.saiful.findbackbd.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        BackBar(title = "Reset Password", onBack = onBack)
        if (!sent) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Enter the email or phone number associated with your account and we'll reset your password.",
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                AppTextField(
                    value = email,
                    onChange = {
                        email = it
                        error = null
                    },
                    hint = "Email or Phone",
                    icon = Icons.Default.Email
                )
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error ?: "", color = Danger, fontSize = 13.sp)
                }
                Spacer(Modifier.height(16.dp))
                AppButton(
                    text = "Send Reset Link",
                    onClick = {
                        if (email.isBlank()) {
                            error = "Please enter your email or phone number."
                        } else {
                            vm.sendPasswordReset(email) {
                                sent = true
                            }
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onBack) {
                    Text("Back to Login", color = Green)
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text("Password Reset Ready", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Reset instructions have been processed for $email (local temporary password set to 123456).",
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                AppButton(text = "Back to Login", onClick = onBack)
                TextButton(onClick = { sent = false }) {
                    Text("Didn't receive it? Try again", color = Green)
                }
            }
        }
    }
}
