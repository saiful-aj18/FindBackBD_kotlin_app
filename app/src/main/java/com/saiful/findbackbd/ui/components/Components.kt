package com.saiful.findbackbd.ui.components

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

data class NavItem(val route: String, val label: String, val icon: ImageVector)

fun categoryIcon(c: String): ImageVector = when (c) {
    "Mobile" -> Icons.Outlined.PhoneAndroid; "Bag" -> Icons.Outlined.Work
    "Wallet" -> Icons.Outlined.AccountBalanceWallet; "Keys" -> Icons.Outlined.Key
    "Documents" -> Icons.Outlined.Description; "Electronics" -> Icons.Outlined.Headphones
    "Clothes" -> Icons.Outlined.Checkroom; else -> Icons.Outlined.MoreHoriz
}

@Composable
fun AppButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, outlined: Boolean = false) {
    val m = modifier.fillMaxWidth().height(48.dp)
    if (outlined) OutlinedButton(onClick, m, shape = RoundedCornerShape(12.dp)) { Text(text) }
    else Button(onClick, m, shape = RoundedCornerShape(12.dp)) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun AppTextField(value: String, onChange: (String) -> Unit, hint: String, icon: ImageVector? = null,
                 password: Boolean = false, modifier: Modifier = Modifier, minLines: Int = 1) {
    var show by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = modifier.fillMaxWidth(),
        placeholder = { Text(hint, fontSize = 13.sp) },
        leadingIcon = if (icon != null) ({ Icon(icon, null, Modifier.size(20.dp)) }) else null,
        trailingIcon = if (password) ({
            IconButton({ show = !show }) { Icon(if (show) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null) }
        }) else null,
        visualTransformation = if (password && !show) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = minLines == 1, minLines = minLines, shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun BackBar(title: String, onBack: () -> Unit, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().background(GreenLight, RoundedCornerShape(12.dp)).padding(4.dp)) {
        options.forEachIndexed { i, t ->
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (i == selected) Green else Color.Transparent)
                    .clickable { onSelect(i) }.padding(vertical = 10.dp), Alignment.Center
            ) { Text(t, color = if (i == selected) Color.White else Green, fontWeight = FontWeight.Medium) }
        }
    }
}

@Composable
fun StatusChip(lost: Boolean) {
    val c = if (lost) Danger else Green
    Text(if (lost) "Lost" else "Found", color = c, fontSize = 11.sp, fontWeight = FontWeight.Medium,
        modifier = Modifier.background(c.copy(alpha = .12f), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 3.dp))
}

@Composable
fun ItemCard(item: LostFoundItem, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).background(GreenLight, RoundedCornerShape(12.dp)), Alignment.Center) {
                Icon(categoryIcon(item.category), null, tint = Green)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold)
                Text("${if (item.isLost) "Lost" else "Found"} · ${item.place}", fontSize = 12.sp, color = TextGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(item.distance, fontSize = 12.sp); Text(item.time, fontSize = 11.sp, color = TextGray)
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, onClick: () -> Unit) {
    Column(Modifier.width(72.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(52.dp).background(GreenLight, CircleShape), Alignment.Center) { Icon(categoryIcon(name), null, tint = Green) }
        Spacer(Modifier.height(4.dp)); Text(name, fontSize = 11.sp)
    }
}

@Composable
fun MenuRow(icon: ImageVector, title: String, onClick: () -> Unit = {}, trailing: String? = null) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Green); Spacer(Modifier.width(14.dp))
        Text(title, Modifier.weight(1f))
        if (trailing != null) Text(trailing, color = TextGray, fontSize = 13.sp)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextGray)
    }
}

@Composable
fun SwitchRow(icon: ImageVector, title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Green); Spacer(Modifier.width(14.dp))
        Text(title, Modifier.weight(1f)); Switch(checked, onChange)
    }
}

@Composable
fun SectionTitle(t: String) = Text(t, color = TextGray, fontSize = 13.sp, modifier = Modifier.padding(16.dp, 14.dp, 16.dp, 2.dp))

@Composable
fun AppBottomBar(items: List<NavItem>, current: String?, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            NavigationBarItem(current == item.route, { onSelect(item.route) }, { Icon(item.icon, null) }, label = { Text(item.label, fontSize = 11.sp) })
        }
    }
}
