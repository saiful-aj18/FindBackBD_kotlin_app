package com.saiful.findbackbd.ui.screens.report

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
fun CreateReportScreen(onBack: () -> Unit, onSubmit: () -> Unit) {
    var lost by remember { mutableStateOf(true) }
    var cat by remember { mutableStateOf("") }; var name by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }
    var menu by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackBar("Create Report", onBack)
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Segmented(listOf("Lost", "Found"), if (lost) 0 else 1) { lost = it == 0 }
            Text("Item Category", fontSize = 13.sp, color = TextGray)
            Box {
                OutlinedButton({ menu = true }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)) {
                    Text(cat.ifEmpty { "Select category" }, Modifier.weight(1f)); Icon(Icons.Outlined.KeyboardArrowDown, null)
                }
                DropdownMenu(menu, { menu = false }) {
                    SampleData.categories.forEach { c -> DropdownMenuItem({ Text(c) }, { cat = c; menu = false }) }
                }
            }
            Text("Item Name", fontSize = 13.sp, color = TextGray)
            AppTextField(name, { name = it }, "e.g. Wallet")
            Text("Description", fontSize = 13.sp, color = TextGray)
            AppTextField(desc, { desc = it }, "Describe the item in detail...", minLines = 4)
            Text("Images", fontSize = 13.sp, color = TextGray)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // TODO: CameraX + photo picker, upload to Firebase Storage
                listOf(Icons.Outlined.CameraAlt to "Camera", Icons.Outlined.PhotoLibrary to "Gallery", Icons.Outlined.AddCircle to "Add (0/5)").forEach { (ic, t) ->
                    Column(Modifier.weight(1f).height(80.dp).border(1.dp, Color(0xFFDDE5E1), RoundedCornerShape(12.dp)).clickable { },
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(ic, null, tint = Green); Text(t, fontSize = 11.sp)
                    }
                }
            }
            Text("Location", fontSize = 13.sp, color = TextGray)
            Row(Modifier.fillMaxWidth().border(1.dp, Color(0xFFDDE5E1), RoundedCornerShape(12.dp)).clickable { }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = Green); Spacer(Modifier.width(8.dp))
                Text("Select location on map", color = TextGray, modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextGray)
            }
            AppButton("Submit", onSubmit) // TODO: ReportViewModel -> Firestore items/{itemId}
        }
    }
}
