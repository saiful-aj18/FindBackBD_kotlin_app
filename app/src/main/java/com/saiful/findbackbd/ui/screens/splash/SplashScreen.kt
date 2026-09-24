package com.saiful.findbackbd.ui.screens.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.TextGray
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.LocationOn, null, Modifier.size(72.dp), tint = Green)
        Spacer(Modifier.height(12.dp))
        Text("FindBack BD", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Green)
        Text("Lost & Found, Together", color = TextGray, fontSize = 13.sp)
    }
}