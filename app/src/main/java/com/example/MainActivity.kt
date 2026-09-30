package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddAnimalDialog
import com.example.ui.components.AddGestationDialog
import com.example.ui.components.AddMedicalDialog
import com.example.ui.components.BottomNav
import com.example.ui.components.QRCodeDialog
import com.example.ui.components.RecordLambingDialog
import com.example.ui.components.RoleSwitchSheet
import com.example.ui.components.TopFarmBar
import com.example.ui.components.WatermarkPreviewDialog
import com.example.ui.screens.AnimalDetailScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinancialScreen
import com.example.ui.screens.GestationScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FarmScreen
import com.example.ui.viewmodel.FarmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OvinaFarmApp()
            }
        }
    }
}

@Composable
fun OvinaFarmApp(viewModel: FarmViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()

    val allAnimals by viewModel.allAnimals.collectAsStateWithLifecycle()
    val filteredAnimals by viewModel.filteredAnimals.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val allMedicalLogs by viewModel.allMedicalLogs.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val valuationSummary by viewModel.valuationSummary.collectAsStateWithLifecycle()

    val selectedAnimalTag by viewModel.selectedAnimalTag.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()

    // Dialog states
    val showAddAnimal by viewModel.showAddAnimalDialog.collectAsStateWithLifecycle()
    val showAddGestation by viewModel.showAddGestationDialog.collectAsStateWithLifecycle()
    val recordLambingTarget by viewModel.showRecordLambingDialog.collectAsStateWithLifecycle()
    val addMedicalTarget by viewModel.showAddMedicalDialog.collectAsStateWithLifecycle()
    val qrCodeAnimal by viewModel.showQRCodeDialog.collectAsStateWithLifecycle()
    val watermarkBitmap by viewModel.showWatermarkDialog.collectAsStateWithLifecycle()
    val showRoleSwitchSheet by viewModel.showRoleSwitchSheet.collectAsStateWithLifecycle()

    val selectedAnimal = allAnimals.find { it.tagId == selectedAnimalTag }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopFarmBar(
                activeRole = currentUserRole,
                onRoleClick = { viewModel.toggleRoleSwitchSheet(true) }
            )
        },
        bottomBar = {
            BottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { viewModel.setScreen(it) }
            )
        }
    ) { innerPadding ->
        when (currentScreen) {
            FarmScreen.DASHBOARD -> {
                DashboardScreen(
                    viewModel = viewModel,
                    animals = allAnimals,
                    groups = allGroups,
                    onNavigate = { viewModel.setScreen(it) },
                    onSelectAnimal = { viewModel.selectAnimal(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            FarmScreen.INVENTORY -> {
                InventoryScreen(
                    viewModel = viewModel,
                    animals = filteredAnimals,
                    searchQuery = searchQuery,
                    categoryFilter = categoryFilter,
                    onSelectAnimal = { viewModel.selectAnimal(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            FarmScreen.ANIMAL_DETAIL -> {
                if (selectedAnimal != null) {
                    val logsForAnimal = allMedicalLogs.filter { it.animalTag == selectedAnimal.tagId }
                    val offspring = allAnimals.filter { it.damTag == selectedAnimal.tagId || it.sireTag == selectedAnimal.tagId }
                    AnimalDetailScreen(
                        viewModel = viewModel,
                        animal = selectedAnimal,
                        medicalLogs = logsForAnimal,
                        offspringList = offspring,
                        userRole = currentUserRole,
                        onBack = { viewModel.setScreen(FarmScreen.INVENTORY) },
                        onSelectTag = { viewModel.selectAnimal(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    // Fallback to inventory
                    InventoryScreen(
                        viewModel = viewModel,
                        animals = filteredAnimals,
                        searchQuery = searchQuery,
                        categoryFilter = categoryFilter,
                        onSelectAnimal = { viewModel.selectAnimal(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            FarmScreen.GESTATION -> {
                val pregnantList = allAnimals.filter { it.reproductiveStatus == "pregnant" }
                GestationScreen(
                    viewModel = viewModel,
                    pregnantAnimals = pregnantList,
                    onSelectAnimal = { viewModel.selectAnimal(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            FarmScreen.FINANCIAL -> {
                FinancialScreen(
                    viewModel = viewModel,
                    userRole = currentUserRole,
                    valuation = valuationSummary,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            FarmScreen.CHAT -> {
                ChatScreen(
                    viewModel = viewModel,
                    messages = chatMessages,
                    animals = allAnimals,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            FarmScreen.SCANNER -> {
                ScannerScreen(
                    animals = allAnimals,
                    onSelectAnimal = {
                        viewModel.selectAnimal(it)
                    },
                    onBack = {
                        viewModel.setScreen(FarmScreen.DASHBOARD)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    // Modal Dialogs
    if (showAddAnimal) {
        AddAnimalDialog(
            groups = allGroups,
            onAdd = { viewModel.addAnimal(it) },
            onDismiss = { viewModel.setShowAddAnimalDialog(false) }
        )
    }

    if (showAddGestation) {
        AddGestationDialog(
            initialEweTag = selectedAnimalTag ?: "",
            onConfirm = { eweTag, ramTag, serviceDate, notes ->
                viewModel.addGestation(eweTag, ramTag, serviceDate, notes)
            },
            onDismiss = { viewModel.setShowAddGestationDialog(false) }
        )
    }

    recordLambingTarget?.let { targetTag ->
        RecordLambingDialog(
            eweTag = targetTag,
            onRecord = { deliveryDate, lambs ->
                viewModel.recordLambing(targetTag, deliveryDate, lambs)
            },
            onDismiss = { viewModel.setRecordLambingTarget(null) }
        )
    }

    addMedicalTarget?.let { targetTag ->
        AddMedicalDialog(
            animalTag = targetTag,
            onSave = { treatment, batch, dosage, booster, notes ->
                viewModel.addMedicalLog(targetTag, treatment, batch, dosage, booster, notes)
            },
            onDismiss = { viewModel.setAddMedicalTarget(null) }
        )
    }

    qrCodeAnimal?.let { animal ->
        QRCodeDialog(
            animal = animal,
            onDismiss = { viewModel.setShowQRCodeDialog(null) }
        )
    }

    watermarkBitmap?.let { bitmap ->
        WatermarkPreviewDialog(
            bitmap = bitmap,
            onDismiss = { viewModel.setWatermarkedPhoto(null) }
        )
    }

    if (showRoleSwitchSheet) {
        RoleSwitchSheet(
            currentRole = currentUserRole,
            onRoleSelected = { viewModel.setUserRole(it) },
            onDismiss = { viewModel.toggleRoleSwitchSheet(false) }
        )
    }
}
