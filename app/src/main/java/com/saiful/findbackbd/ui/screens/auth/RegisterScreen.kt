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
fun RegisterScreen(onBack: () -> Unit, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }; var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackBar("Create Your Account", onBack)
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppTextField(name, { name = it }, "Full Name", Icons.Outlined.Person)
            AppTextField(email, { email = it }, "Email or Phone", Icons.Outlined.Email)
            AppTextField(pass, { pass = it }, "Password", Icons.Outlined.Lock, password = true)
            AppTextField(confirm, { confirm = it }, "Confirm Password", Icons.Outlined.Lock, password = true)
            error?.let { Text(it, color = Danger, fontSize = 13.sp) }
            AppButton("Register", {
                error = when {
                    name.isBlank() || email.isBlank() -> "Please fill all fields."
                    pass.length < 6 -> "Password must be at least 6 characters."
                    pass != confirm -> "Passwords do not match."
                    else -> null
                }
                // New accounts are always Role.USER. Admin accounts are created only from Firebase console.
                if (error == null) onDone()
            })
            TextButton(onBack, Modifier.align(Alignment.CenterHorizontally)) { Text("Already have an account? Login") }
        }
    }
}
