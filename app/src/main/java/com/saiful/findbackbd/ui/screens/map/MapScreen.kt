package com.saiful.findbackbd.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.components.StatusChip
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    onBack: () -> Unit = {},
    onDetails: (String) -> Unit = {},
    vm: HomeViewModel = hiltViewModel()
) {
    val allItems by vm.items.collectAsState()
    var filterStatus by remember { mutableStateOf("All") }

    val visibleItems = remember(allItems, filterStatus) {
        allItems.filter { item ->
            when (filterStatus) {
                "Lost" -> item.isLost && !item.isResolved
                "Found" -> !item.isLost && !item.isResolved
                else -> !item.isResolved
            }
        }
    }

    var selectedItem by remember(visibleItems) {
        mutableStateOf(visibleItems.firstOrNull())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackBar(title = "Live Reports Map (${visibleItems.size})", onBack = onBack)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Lost", "Found").forEach { tab ->
                FilterChip(
                    selected = filterStatus == tab,
                    onClick = { filterStatus = tab },
                    label = { Text(tab, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Green,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFE4F2EC))
        ) {
            // Stylized Dhaka street grid & river map canvas so pins always render clearly even without a billing Maps key
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 70f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0xFFCBE2D6),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 2f
                    )
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0xFFCBE2D6),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 2f
                    )
                    y += step
                }
                // Major avenue lines
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(0f, size.height * 0.45f),
                    end = Offset(size.width, size.height * 0.55f),
                    strokeWidth = 12f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(size.width * 0.48f, 0f),
                    end = Offset(size.width * 0.52f, size.height),
                    strokeWidth = 12f
                )
            }

            // Map pins positioned dynamically from latitude/longitude
            visibleItems.forEachIndexed { idx, item ->
                val normX = (((item.longitude - 90.35) / 0.12).coerceIn(0.12, 0.85)).toFloat()
                val normY = (((23.88 - item.latitude) / 0.16).coerceIn(0.12, 0.82)).toFloat()
                val isSelected = selectedItem?.id == item.id

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset {
                            IntOffset(
                                x = (normX * 720 + (idx % 3) * 22).roundToInt(),
                                y = (normY * 760 + (idx % 2) * 18).roundToInt()
                            )
                        }
                        .clickable { selectedItem = item },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF121816) else Color.White)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.name.take(14),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF121816)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = item.name,
                        tint = if (item.isLost) Danger else Green,
                        modifier = Modifier.size(if (isSelected) 40.dp else 32.dp)
                    )
                }
            }
        }

        selectedItem?.let { current ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(current.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("${current.place} • ${current.distance}", color = TextGray, fontSize = 13.sp)
                        }
                        StatusChip(lost = current.isLost, isResolved = current.isResolved)
                    }
                    Spacer(Modifier.height(10.dp))
                    AppButton(
                        text = "View Full Report & Contact",
                        onClick = { onDetails(current.id) }
                    )
                }
            }
        }
    }
}
