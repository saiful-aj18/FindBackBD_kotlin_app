package com.saiful.findbackbd.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.components.MenuRow
import com.saiful.findbackbd.ui.components.SectionTitle
import com.saiful.findbackbd.ui.components.SwitchRow
import com.saiful.findbackbd.ui.screens.auth.AuthViewModel
import com.saiful.findbackbd.ui.theme.AppSettings
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    authVm: AuthViewModel = hiltViewModel()
) {
    val currentUser by authVm.currentUser.collectAsState()
    var push by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("English") }

    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogBody by remember { mutableStateOf("") }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    var editName by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var editEmail by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var editPhone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    if (showEditProfile) {
        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(value = editName, onChange = { editName = it }, hint = "Full Name", icon = Icons.Default.Person)
                    AppTextField(value = editEmail, onChange = { editEmail = it }, hint = "Email", icon = Icons.Default.Email)
                    AppTextField(value = editPhone, onChange = { editPhone = it }, hint = "Phone", icon = Icons.Default.Phone)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authVm.updateProfile(editName, editEmail, editPhone) {
                            showEditProfile = false
                            statusBanner = "Profile updated successfully."
                        }
                    }
                ) {
                    Text("Save", color = Green, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfile = false }) { Text("Cancel") }
            }
        )
    }

    if (showChangePassword) {
        AlertDialog(
            onDismissRequest = { showChangePassword = false },
            title = { Text("Change Password", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTextField(
                        value = currentPassword,
                        onChange = { currentPassword = it; passwordError = null },
                        hint = "Current Password",
                        icon = Icons.Default.Lock,
                        password = true
                    )
                    AppTextField(
                        value = newPassword,
                        onChange = { newPassword = it; passwordError = null },
                        hint = "New Password (min 6 chars)",
                        icon = Icons.Default.Lock,
                        password = true
                    )
                    if (passwordError != null) {
                        Text(passwordError ?: "", color = Danger, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authVm.changePassword(currentPassword, newPassword) { result ->
                            result.onSuccess {
                                showChangePassword = false
                                currentPassword = ""
                                newPassword = ""
                                statusBanner = "Password updated successfully."
                            }.onFailure { err ->
                                passwordError = err.message ?: "Could not update password"
                            }
                        }
                    }
                ) {
                    Text("Update Password", color = Green, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePassword = false }) { Text("Cancel") }
            }
        )
    }

    if (infoDialogTitle != null) {
        AlertDialog(
            onDismissRequest = { infoDialogTitle = null },
            title = { Text(infoDialogTitle ?: "", fontWeight = FontWeight.Bold) },
            text = { Text(infoDialogBody, fontSize = 14.sp, color = TextGray) },
            confirmButton = {
                TextButton(onClick = { infoDialogTitle = null }) {
                    Text("Close", color = Green, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        BackBar(title = "Settings", onBack = onBack)

        if (statusBanner != null) {
            Text(
                text = statusBanner ?: "",
                color = Green,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        SectionTitle("Account (${currentUser?.email ?: "Signed In"})")
        MenuRow(
            icon = Icons.Default.Person,
            title = "Edit Profile",
            trailing = currentUser?.name ?: "",
            onClick = {
                editName = currentUser?.name ?: ""
                editEmail = currentUser?.email ?: ""
                editPhone = currentUser?.phone ?: ""
                showEditProfile = true
            }
        )
        MenuRow(
            icon = Icons.Default.Lock,
            title = "Change Password",
            onClick = {
                passwordError = null
                showChangePassword = true
            }
        )

        SectionTitle("Preferences")
        SwitchRow(
            icon = Icons.Default.DarkMode,
            title = "Dark Mode",
            checked = AppSettings.dark,
            onChange = { AppSettings.dark = it }
        )
        MenuRow(
            icon = Icons.Default.Language,
            title = "Language",
            trailing = language,
            onClick = {
                language = if (language == "English") "বাংলা (Bangla)" else "English"
            }
        )

        SectionTitle("Notifications")
        SwitchRow(
            icon = Icons.Default.Notifications,
            title = "Push Notifications",
            checked = push,
            onChange = { push = it }
        )

        SectionTitle("About")
        MenuRow(
            icon = Icons.Default.Help,
            title = "Help & Support",
            onClick = {
                infoDialogTitle = "Help & Support"
                infoDialogBody = "Need assistance recovering an item or reporting an issue?\n\n• Email: support@findback.bd\n• Helpline: +880 1700 000000\n• Always meet in safe public places when handing over found items."
            }
        )
        MenuRow(
            icon = Icons.Default.Info,
            title = "About FindBack BD",
            trailing = "v1.0",
            onClick = {
                infoDialogTitle = "FindBack BD v1.0"
                infoDialogBody = "FindBack BD is a community-powered Lost & Found platform for Bangladesh. Report lost or found items, get automatic smart match alerts, and coordinate safely via direct chat."
            }
        )
        Box(modifier = Modifier.padding(16.dp)) {
            AppButton(text = "Logout", onClick = onLogout, outlined = true)
        }
    }
}
