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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.NewbornInfo
import com.example.ui.theme.EmeraldPrimaryLight

@Composable
fun RecordLambingDialog(
    eweTag: String,
    onRecord: (deliveryDate: Long, lambs: List<NewbornInfo>) -> Unit,
    onDismiss: () -> Unit
) {
    var count by remember { mutableIntStateOf(1) } // 1, 2, or 3
    var lamb1Tag by remember { mutableStateOf("LAMB-${eweTag.takeLast(4)}-1") }
    var lamb1Gender by remember { mutableStateOf("male") }
    var lamb1Weight by remember { mutableStateOf("4.2") }

    var lamb2Tag by remember { mutableStateOf("LAMB-${eweTag.takeLast(4)}-2") }
    var lamb2Gender by remember { mutableStateOf("female") }
    var lamb2Weight by remember { mutableStateOf("3.9") }

    var lamb3Tag by remember { mutableStateOf("LAMB-${eweTag.takeLast(4)}-3") }
    var lamb3Gender by remember { mutableStateOf("male") }
    var lamb3Weight by remember { mutableStateOf("3.5") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("record_lambing_dialog"),
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
                    text = "Record Lambing Event • $eweTag",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Select Offspring Prolificacy Count:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = count == 1,
                        onClick = { count = 1 },
                        label = { Text("Single (1)") }
                    )
                    FilterChip(
                        selected = count == 2,
                        onClick = { count = 2 },
                        label = { Text("Twins (2)") }
                    )
                    FilterChip(
                        selected = count == 3,
                        onClick = { count = 3 },
                        label = { Text("Triplets (3)") }
                    )
                }

                // Lamb 1 Inputs
                Text(
                    text = "Newborn #1 Details",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = lamb1Tag,
                        onValueChange = { lamb1Tag = it.uppercase() },
                        label = { Text("Ear Tag") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lamb1Gender,
                        onValueChange = { lamb1Gender = it },
                        label = { Text("M/F") },
                        modifier = Modifier.weight(0.8f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lamb1Weight,
                        onValueChange = { lamb1Weight = it },
                        label = { Text("Kg") },
                        modifier = Modifier.weight(0.8f),
                        singleLine = true
                    )
                }

                // Lamb 2 Inputs (if twins or triplets)
                if (count >= 2) {
                    Text(
                        text = "Newborn #2 Details",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = lamb2Tag,
                            onValueChange = { lamb2Tag = it.uppercase() },
                            label = { Text("Ear Tag") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lamb2Gender,
                            onValueChange = { lamb2Gender = it },
                            label = { Text("M/F") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lamb2Weight,
                            onValueChange = { lamb2Weight = it },
                            label = { Text("Kg") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }
                }

                // Lamb 3 Inputs (if triplets)
                if (count >= 3) {
                    Text(
                        text = "Newborn #3 Details",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = lamb3Tag,
                            onValueChange = { lamb3Tag = it.uppercase() },
                            label = { Text("Ear Tag") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lamb3Gender,
                            onValueChange = { lamb3Gender = it },
                            label = { Text("M/F") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lamb3Weight,
                            onValueChange = { lamb3Weight = it },
                            label = { Text("Kg") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val lambs = mutableListOf<NewbornInfo>()
                            lambs.add(
                                NewbornInfo(
                                    tagId = lamb1Tag.trim(),
                                    gender = if (lamb1Gender.contains("f", ignoreCase = true)) "female" else "male",
                                    weightKg = lamb1Weight.toDoubleOrNull() ?: 4.0
                                )
                            )
                            if (count >= 2) {
                                lambs.add(
                                    NewbornInfo(
                                        tagId = lamb2Tag.trim(),
                                        gender = if (lamb2Gender.contains("f", ignoreCase = true)) "female" else "male",
                                        weightKg = lamb2Weight.toDoubleOrNull() ?: 3.8
                                    )
                                )
                            }
                            if (count >= 3) {
                                lambs.add(
                                    NewbornInfo(
                                        tagId = lamb3Tag.trim(),
                                        gender = if (lamb3Gender.contains("f", ignoreCase = true)) "female" else "male",
                                        weightKg = lamb3Weight.toDoubleOrNull() ?: 3.5
                                    )
                                )
                            }
                            onRecord(System.currentTimeMillis(), lambs)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Lambing")
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
