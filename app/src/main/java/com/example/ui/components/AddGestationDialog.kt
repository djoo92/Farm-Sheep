package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.domain.GestationEngine
import com.example.ui.theme.Emerald80
import com.example.ui.theme.EmeraldPrimaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddGestationDialog(
    initialEweTag: String = "",
    onConfirm: (eweTag: String, ramTag: String?, serviceDate: Long, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var eweTag by remember { mutableStateOf(initialEweTag) }
    var ramTag by remember { mutableStateOf("LEG-RAM-01") }
    var daysAgoText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("Ultrasound confirmed pregnancy. Moved to observation protocol.") }

    val daysAgo = daysAgoText.toIntOrNull() ?: 0
    val calculatedServiceDate = System.currentTimeMillis() - (daysAgo * 86_400_000L)
    val projection = remember(calculatedServiceDate) {
        GestationEngine.calculate(calculatedServiceDate)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_gestation_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Log Insemination / Service (150d)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = eweTag,
                    onValueChange = { eweTag = it.uppercase() },
                    label = { Text("Ewe Ear Tag ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ramTag,
                    onValueChange = { ramTag = it.uppercase() },
                    label = { Text("Sire / Ram Ear Tag") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = daysAgoText,
                    onValueChange = { daysAgoText = it },
                    label = { Text("Days Since Service (0 = today)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Projected Lambing Preview Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Biological 150-Day Projection",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Expected Lambing: ${projection.formattedExpectedDate}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Emerald80
                        )
                        Text(
                            text = "${projection.daysRemaining} days remaining (${projection.progressPercent}% gestation)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Ultrasound Confirmation") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (eweTag.isNotBlank()) {
                                onConfirm(eweTag.trim(), ramTag.trim().ifBlank { null }, calculatedServiceDate, notes)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Register Gestation")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
