package com.saiful.findbackbd.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saiful.findbackbd.data.model.SampleData
import com.saiful.findbackbd.ui.components.BackBar
import com.saiful.findbackbd.ui.screens.home.HomeViewModel
import com.saiful.findbackbd.ui.theme.Green
import com.saiful.findbackbd.ui.theme.GreenLight
import com.saiful.findbackbd.ui.theme.TextGray
import com.saiful.findbackbd.utils.MatchUtils

@Composable
fun AdminStatsScreen(
    onBack: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val items by vm.items.collectAsState()
    val users by vm.allUsers.collectAsState()

    val matchesCount = remember(items) {
        val lost = items.filter { it.isLost }
        val found = items.filter { !it.isLost }
        var count = 0
        for (l in lost) {
            for (f in found) {
                if (MatchUtils.score(l, f) >= 40) count++
            }
        }
        count
    }

    val stats = listOf(
        "Registered Users" to users.size.toString(),
        "Total Reports" to items.size.toString(),
        "Smart Matches" to matchesCount.toString(),
        "Recovered Items" to items.count { it.isResolved }.toString(),
        "Active Lost" to items.count { it.isLost && !it.isResolved }.toString(),
        "Active Found" to items.count { !it.isLost && !it.isResolved }.toString()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        BackBar(title = "Live Platform Analytics", onBack = onBack)
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stats.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (k, v) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenLight)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(v, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Green)
                                Text(k, color = TextGray, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Category Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            SampleData.categories.forEach { category ->
                val count = items.count { it.category.equals(category, ignoreCase = true) }
                val fraction = if (items.isNotEmpty()) count.toFloat() / items.size.toFloat() else 0f
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(category, modifier = Modifier.weight(1f), fontSize = 13.sp)
                        Text("$count reports", color = TextGray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Green,
                        trackColor = GreenLight
                    )
                }
            }
        }
    }
}
