package com.saiful.findbackbd.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.ui.components.ItemCard

@Composable
fun SearchScreen(
    onCreate: () -> Unit,
    onDetails: (String) -> Unit,
    onMap: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {

    val items by vm.items.collectAsState()

    LaunchedEffect(Unit) {
        vm.load()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreate
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create"
                )
            }
        }
    ) { pad ->

        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    "FindBack BD",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    "Find what was lost. Return what was found."
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onMap,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = "Map"
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text("Open Map")
                }
            }

            items(items) { item ->
                ItemCard(item) {
                    onDetails(item.id)
                }
            }
        }
    }
}