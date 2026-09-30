package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimalEntity
import com.example.data.model.HerdGroupEntity
import com.example.ui.components.GestationAlertCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.Emerald80
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.SkyHighlight
import com.example.ui.viewmodel.FarmScreen
import com.example.ui.viewmodel.FarmViewModel

@Composable
fun DashboardScreen(
    viewModel: FarmViewModel,
    animals: List<AnimalEntity>,
    groups: List<HerdGroupEntity>,
    onNavigate: (FarmScreen) -> Unit,
    onSelectAnimal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = animals.size
    val pregnantAnimals = animals.filter { it.reproductiveStatus == "pregnant" }
    val lambsCount = animals.count { it.category.contains("lamb") }
    val ramsCount = animals.count { it.gender == "male" && it.category.contains("ram") }
    val ewesCount = animals.count { it.gender == "female" && it.category.contains("ewe") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Quick Status Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Flock Operations Control",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Master herd status • 59 verified heads",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Scan / Fast Search CTA
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(FarmScreen.SCANNER) }
                        .testTag("fast_scan_btn"),
                    color = EmeraldPrimaryLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Tag",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Scan Tag",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 4 KPI Metric Cards in 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "TOTAL INVENTORY",
                        value = "$totalCount",
                        subtitle = "$ewesCount Ewes • $ramsCount Rams",
                        icon = Icons.Default.Pets,
                        iconTint = Emerald80,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "GESTATION ALERT",
                        value = "${pregnantAnimals.size}",
                        subtitle = "Due in 10 & 18 days",
                        icon = Icons.Default.HourglassTop,
                        iconTint = AmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "NURSING LAMBS",
                        value = "$lambsCount",
                        subtitle = "Includes yesterday twins",
                        icon = Icons.Default.ChildCare,
                        iconTint = SkyHighlight,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "BREEDING SIRES",
                        value = "$ramsCount",
                        subtitle = "1 Stud + 1 Juvenile",
                        icon = Icons.Default.Male,
                        iconTint = RoseAlert,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Operational Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.setShowAddAnimalDialog(true) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Add Sheep", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { viewModel.setShowAddGestationDialog(true) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Log Mating", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { viewModel.createAndSendWatermarkedPhoto("LEG-PRIME-01", "Morning clinical inspection: Udder enlargement verified.") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Photo Log", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Imminent Gestations Section (150-Day Biological Tracker)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(RoseAlert)
                    )
                    Text(
                        text = "Imminent Lambings (Critical Countdown)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Emerald80
                    ),
                    modifier = Modifier.clickable { onNavigate(FarmScreen.GESTATION) }
                )
            }
        }

        // Render top pregnant ewes
        items(pregnantAnimals.take(4)) { pregnantEwe ->
            GestationAlertCard(
                animal = pregnantEwe,
                onRecordLambing = { tag ->
                    viewModel.setRecordLambingTarget(tag)
                },
                onClick = { onSelectAnimal(pregnantEwe.tagId) }
            )
        }

        // Herd Cohorts Summary
        item {
            Text(
                text = "Registered Cohorts & Batches",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(groups) { group ->
            val countInGroup = animals.count { it.groupId == group.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        viewModel.setCategoryFilter(
                            when (group.id) {
                                "grp_ancient" -> "LEGACY"
                                "grp_nouveaux_c1" -> "COHORT_1"
                                "grp_nouveaux_c2" -> "COHORT_2"
                                else -> "ALL"
                            }
                        )
                        onNavigate(FarmScreen.INVENTORY)
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = group.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "$countInGroup Heads",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
