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

data class AuthUiState(
 val role: Role = Role.USER, val email: String = "", val password: String = "", val error: String? = null
)

class AuthViewModel : ViewModel() {
 private val _ui = MutableStateFlow(AuthUiState())
 val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

 fun setRole(r: Role) = _ui.update { it.copy(role = r, error = null) }
 fun onEmail(v: String) = _ui.update { it.copy(email = v, error = null) }
 fun onPassword(v: String) = _ui.update { it.copy(password = v, error = null) }
 fun reset() { _ui.value = AuthUiState() }

 /** Demo login. Admin demo account: admin@findback.bd / admin123 (any user: any email + 6+ char password). */
 fun login(onSuccess: (Role) -> Unit) {
  val s = _ui.value
  if (s.email.isBlank() || s.password.length < 6) {
   _ui.update { it.copy(error = "Enter a valid email/phone and a password of at least 6 characters.") }; return
  }
  // TODO: FirebaseAuth.signInWithEmailAndPassword(...) then read users/{uid}.role from Firestore
  val real = if (s.email.trim() == "admin@findback.bd" && s.password == "admin123") Role.ADMIN else Role.USER
  if (s.role == Role.ADMIN && real != Role.ADMIN) {
   _ui.update { it.copy(error = "This account has no admin access.") }; return
  }
  if (s.role == Role.USER && real == Role.ADMIN) {
   _ui.update { it.copy(error = "This is an admin account. Please use Admin Login.") }; return
  }
  onSuccess(real)
 }
}
