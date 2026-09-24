package com.saiful.findbackbd.ui.screens.details

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
fun ItemDetailsScreen(id: String, onBack: () -> Unit, onMessage: (String) -> Unit) {
    val item = SampleData.items.firstOrNull { it.id == id } ?: SampleData.items.first()
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize()) {
        // Map placeholder. TODO: replace with GoogleMap composable (maps-compose) + MAPS_API_KEY
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFE3ECE7))) {
            Canvas(Modifier.fillMaxSize()) {
                for (i in 1..8) {
                    drawLine(Color.White, Offset(size.width * i / 9, 0f), Offset(size.width * i / 9, size.height), 6f)
                    drawLine(Color.White, Offset(0f, size.height * i / 9), Offset(size.width, size.height * i / 9), 6f)
                }
            }
            Icon(Icons.Outlined.LocationOn, null, Modifier.align(Alignment.Center).size(48.dp), tint = Danger)
            IconButton(onBack, Modifier.padding(8.dp).align(Alignment.TopStart).background(Color.White, CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
            }
        }
        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(categoryIcon(item.category), null, tint = Green); Spacer(Modifier.width(8.dp))
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f)); StatusChip(item.isLost)
            }
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.LocationOn, null, Modifier.size(16.dp), tint = TextGray); Text(" ${item.place}, Dhaka", fontSize = 13.sp, color = TextGray) }
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.AccessTime, null, Modifier.size(16.dp), tint = TextGray); Text(" ${item.time}", fontSize = 13.sp, color = TextGray) }
            Text(item.description, fontSize = 13.sp)
            Text("Contact", fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).background(GreenLight, CircleShape), Alignment.Center) { Text(item.contact.take(1), color = Green, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(10.dp))
                Column { Text(item.contact, fontWeight = FontWeight.Medium); Text("+880 1712 345678", fontSize = 12.sp, color = TextGray) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppButton("Message", { onMessage(item.contact) }, Modifier.weight(1f))
                AppButton("Call", { ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+8801712345678"))) }, Modifier.weight(1f), outlined = true)
            }
        }
    }
}
