package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AnimalEntity
import com.example.data.model.HerdGroupEntity
import com.example.ui.theme.EmeraldPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnimalDialog(
    groups: List<HerdGroupEntity>,
    onAdd: (AnimalEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var tagId by remember { mutableStateOf("") }
    var rfid by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(groups.firstOrNull()?.id ?: "grp_ancient") }
    var selectedCategory by remember { mutableStateOf("primiparous_ewe") }
    var gender by remember { mutableStateOf("female") }
    var weightText by remember { mutableStateOf("52.0") }
    var purchasePriceText by remember { mutableStateOf("0") }
    var damTag by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var groupExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "primiparous_ewe",
        "multiparous_ewe",
        "breeding_ram",
        "juvenile_ram",
        "nursing_lamb_female",
        "nursing_lamb_male",
        "fattening_lamb"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_animal_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Register New Livestock",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = tagId,
                    onValueChange = { tagId = it.uppercase() },
                    label = { Text("Ear Tag ID (e.g. DZ-01-99)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tag_id_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = rfid,
                    onValueChange = { rfid = it },
                    label = { Text("Electronic RFID (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Group selector
                ExposedDropdownMenuBox(
                    expanded = groupExpanded,
                    onExpandedChange = { groupExpanded = !groupExpanded }
                ) {
                    val currentGroupObj = groups.find { it.id == selectedGroup }
                    OutlinedTextField(
                        value = currentGroupObj?.name ?: selectedGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Herd Group / Cohort") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = groupExpanded,
                        onDismissRequest = { groupExpanded = false }
                    ) {
                        groups.forEach { grp ->
                            DropdownMenuItem(
                                text = { Text(grp.name) },
                                onClick = {
                                    selectedGroup = grp.id
                                    groupExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = formatCategory(selectedCategory),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Livestock Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(formatCategory(cat)) },
                                onClick = {
                                    selectedCategory = cat
                                    gender = if (cat.contains("ram") || cat.contains("male")) "male" else "female"
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = gender.replaceFirstChar { it.uppercase() },
                        onValueChange = { gender = it.lowercase() },
                        label = { Text("Gender") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = damTag,
                    onValueChange = { damTag = it.uppercase() },
                    label = { Text("Dam Tag (Mother, if known)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = purchasePriceText,
                    onValueChange = { purchasePriceText = it },
                    label = { Text("Acquisition Price (DZD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical or Origin Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (tagId.isNotBlank()) {
                                val groupObj = groups.find { it.id == selectedGroup }
                                val animal = AnimalEntity(
                                    tagId = tagId.trim(),
                                    rfid = rfid.trim(),
                                    groupId = selectedGroup,
                                    groupName = groupObj?.name ?: "General Herd",
                                    category = selectedCategory,
                                    gender = gender.trim().lowercase(),
                                    birthDate = System.currentTimeMillis() - (180 * 86_400_000L),
                                    damTag = if (damTag.isNotBlank()) damTag.trim() else null,
                                    currentWeightKg = weightText.toDoubleOrNull() ?: 50.0,
                                    purchasePriceDzd = purchasePriceText.toDoubleOrNull() ?: 0.0,
                                    currentValuationDzd = (purchasePriceText.toDoubleOrNull() ?: 0.0).coerceAtLeast(60000.0),
                                    notes = notes
                                )
                                onAdd(animal)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_animal_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Register Animal")
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
