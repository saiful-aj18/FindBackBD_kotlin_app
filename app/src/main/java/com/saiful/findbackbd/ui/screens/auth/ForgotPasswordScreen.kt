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
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var sent by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackBar("Reset Password", onBack)

        if (!sent) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Outlined.LockReset, null, Modifier.size(48.dp), tint = Green)
                Text(
                    "Enter the email or phone number associated with your account and we'll send you a link to reset your password.",
                    color = TextGray, fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                AppTextField(email, { email = it; error = null }, "Email or Phone", Icons.Outlined.Email)
                error?.let { Text(it, color = Danger, fontSize = 13.sp) }
                AppButton("Send Reset Link", {
                    error = when {
                        email.isBlank() -> "Please enter your email or phone number."
                        else -> null
                    }
                    // TODO: FirebaseAuth.sendPasswordResetEmail(email) or phone-based reset flow
                    if (error == null) sent = true
                })
                TextButton(onBack, Modifier.align(Alignment.CenterHorizontally)) { Text("Back to Login") }
            }
        } else {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Outlined.MarkEmailRead, null, Modifier.size(56.dp), tint = Green)
                Text("Check Your Inbox", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "We've sent a password reset link to $email. Follow the instructions in the email to set a new password.",
                    color = TextGray, fontSize = 13.sp, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                AppButton("Back to Login", onBack)
                TextButton({ sent = false; email = "" }) { Text("Didn't receive it? Try again") }
            }
        }
    }
}