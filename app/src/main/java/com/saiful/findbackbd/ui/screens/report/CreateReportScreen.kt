package com.saiful.findbackbd.ui.screens.report

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.saiful.findbackbd.data.model.BdLocation
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.ui.components.AppButton
import com.saiful.findbackbd.ui.components.AppTextField
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.components.Segmented
import com.saiful.findbackbd.ui.theme.Danger
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray

@Composable
fun CreateReportScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    vm: ReportViewModel = hiltViewModel()
) {
    val currentUser by vm.currentUser.collectAsState()
    val isSubmitting by vm.isSubmitting.collectAsState()

    var type by remember { mutableIntStateOf(0) }
    var category by remember { mutableStateOf("Mobile") }
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf(SampleData.bdLocations.first().name) }
    var selectedBdLocation by remember { mutableStateOf<BdLocation>(SampleData.bdLocations.first()) }
    var dateText by remember { mutableStateOf("Today") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (contactName.isBlank()) contactName = user.name
            if (contactPhone.isBlank()) contactPhone = user.phone
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        BackBar(title = "Create Report", onBack = onBack)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Segmented(
                options = listOf("Lost Item", "Found Item"),
                selected = type,
                onSelect = { type = it }
            )

            Text("Item Category", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Box {
                OutlinedButton(
                    onClick = { categoryExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = category.ifBlank { "Select category" },
                        modifier = Modifier.weight(1f),
                        color = if (category.isBlank()) TextGray else MaterialTheme.colorScheme.onSurface
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    SampleData.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Item Name *", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            AppTextField(
                value = name,
                onChange = { name = it; error = null },
                hint = "e.g. iPhone 13 Blue, Black Leather Wallet"
            )

            Text("Description *", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            AppTextField(
                value = desc,
                onChange = { desc = it; error = null },
                hint = "Describe distinguishing marks, brand, color, or contents...",
                minLines = 3
            )

            Text("When was it ${if (type == 0) "lost" else "found"}?", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Today", "Yesterday", "2 days ago", "This week").forEach { d ->
                    FilterChip(
                        selected = dateText == d,
                        onClick = { dateText = d },
                        label = { Text(d, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            Text("Location *", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFDCE5E1), RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { locationExpanded = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Green)
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(locationText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(
                            text = "GPS: ${selectedBdLocation.latitude}, ${selectedBdLocation.longitude} (${selectedBdLocation.distanceLabel})",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose area")
                }
                DropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false }
                ) {
                    SampleData.bdLocations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text("${loc.name} (${loc.distanceLabel})") },
                            onClick = {
                                selectedBdLocation = loc
                                locationText = loc.name
                                locationExpanded = false
                            }
                        )
                    }
                }
            }
            AppTextField(
                value = locationText,
                onChange = { locationText = it; error = null },
                hint = "Or type exact landmark / address",
                icon = Icons.Default.LocationOn
            )

            Text("Photo Attachment", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (selectedImageUri.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenLight)
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { selectedImageUri = "" },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = Color.White)
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFDCE5E1), RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Green)
                        Spacer(Modifier.height(4.dp))
                        Text("Pick from Gallery", fontSize = 12.sp, color = TextGray)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFDCE5E1), RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo", tint = Green)
                        Spacer(Modifier.height(4.dp))
                        Text("Add Photo", fontSize = 12.sp, color = TextGray)
                    }
                }
            }

            Text("Your Contact Details", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            AppTextField(
                value = contactName,
                onChange = { contactName = it },
                hint = "Your Name",
                icon = Icons.Default.Person
            )
            AppTextField(
                value = contactPhone,
                onChange = { contactPhone = it },
                hint = "Phone Number",
                icon = Icons.Default.Phone
            )

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = Danger,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(4.dp))
            AppButton(
                text = if (isSubmitting) "Publishing Report..." else "Submit Report",
                enabled = !isSubmitting,
                onClick = {
                    when {
                        name.isBlank() -> error = "Please enter the item name."
                        desc.isBlank() -> error = "Please enter a brief description."
                        locationText.isBlank() -> error = "Please specify where the item was ${if (type == 0) "lost" else "found"}."
                        else -> {
                            vm.create(
                                type = if (type == 0) "lost" else "found",
                                title = name,
                                category = category,
                                description = desc,
                                location = locationText,
                                date = dateText,
                                time = "Just now",
                                contact = contactName,
                                contactPhone = contactPhone,
                                latitude = selectedBdLocation.latitude,
                                longitude = selectedBdLocation.longitude,
                                distance = selectedBdLocation.distanceLabel,
                                imageUrl = selectedImageUri,
                                onSuccess = { onSubmit() },
                                onError = { msg -> error = msg }
                            )
                        }
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
