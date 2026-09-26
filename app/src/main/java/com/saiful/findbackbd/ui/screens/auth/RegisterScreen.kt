package com.saiful.findbackbd.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+880 ") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        BackBar(title = "Create Your Account", onBack = onBack)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextField(
                value = name,
                onChange = { name = it; error = null },
                hint = "Full Name",
                icon = Icons.Default.Person
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = email,
                onChange = { email = it; error = null },
                hint = "Email Address",
                icon = Icons.Default.Email
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = phone,
                onChange = { phone = it; error = null },
                hint = "Phone Number (e.g. +880 17...)",
                icon = Icons.Default.Phone
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = password,
                onChange = { password = it; error = null },
                hint = "Password (min 6 characters)",
                icon = Icons.Default.Lock,
                password = true
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = confirm,
                onChange = { confirm = it; error = null },
                hint = "Confirm Password",
                icon = Icons.Default.Lock,
                password = true
            )
            Spacer(Modifier.height(16.dp))
            if (error != null) {
                Text(
                    text = error ?: "",
                    color = Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            AppButton(
                text = if (isSubmitting) "Creating Account..." else "Register",
                enabled = !isSubmitting,
                onClick = {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() ->
                            error = "Please fill in your name, email, and password."
                        password.length < 6 ->
                            error = "Password must be at least 6 characters."
                        password != confirm ->
                            error = "Passwords do not match."
                        else -> {
                            isSubmitting = true
                            vm.register(
                                name = name,
                                email = email,
                                phone = phone,
                                password = password,
                                onSuccess = {
                                    isSubmitting = false
                                    onDone()
                                },
                                onError = { msg ->
                                    isSubmitting = false
                                    error = msg
                                }
                            )
                        }
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack) {
                Text("Already have an account? Login", color = Green)
            }
        }
    }
}
