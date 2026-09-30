package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.OvinaDatabase
import com.example.data.model.AnimalEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.GestationEntity
import com.example.data.model.HerdGroupEntity
import com.example.data.model.MedicalLogEntity
import com.example.data.repository.FarmRepository
import com.example.data.repository.NewbornInfo
import com.example.domain.GestationEngine
import com.example.domain.ValuationEngine
import com.example.domain.WatermarkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole(val title: String, val canViewFinances: Boolean) {
    SUPER_ADMIN("Super Admin", true),
    FARM_ADMIN("Farm Admin", true),
    VETERINARIAN("Veterinarian", false),
    FIELD_WORKER("Field Worker", false)
}

enum class FarmScreen(val label: String) {
    DASHBOARD("Dashboard"),
    INVENTORY("Inventory"),
    ANIMAL_DETAIL("Livestock Dossier"),
    GESTATION("Gestations"),
    FINANCIAL("Finances"),
    CHAT("Field Relay"),
    SCANNER("QR Scanner")
}

class FarmViewModel(application: Application) : AndroidViewModel(application) {
    private val database = OvinaDatabase.getDatabase(application, viewModelScope)
    private val repository = FarmRepository(database)

    // Current User Role for Granular RBAC
    private val _currentUserRole = MutableStateFlow(UserRole.SUPER_ADMIN)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    // Current Screen
    private val _currentScreen = MutableStateFlow(FarmScreen.DASHBOARD)
    val currentScreen: StateFlow<FarmScreen> = _currentScreen.asStateFlow()

    private val _selectedAnimalTag = MutableStateFlow<String?>(null)
    val selectedAnimalTag: StateFlow<String?> = _selectedAnimalTag.asStateFlow()

    // Search and filters for Inventory
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow("ALL")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    // Dialog & Flow States
    private val _showAddAnimalDialog = MutableStateFlow(false)
    val showAddAnimalDialog: StateFlow<Boolean> = _showAddAnimalDialog.asStateFlow()

    private val _showAddGestationDialog = MutableStateFlow(false)
    val showAddGestationDialog: StateFlow<Boolean> = _showAddGestationDialog.asStateFlow()

    private val _showRecordLambingDialog = MutableStateFlow<String?>(null)
    val showRecordLambingDialog: StateFlow<String?> = _showRecordLambingDialog.asStateFlow()

    private val _showAddMedicalDialog = MutableStateFlow<String?>(null)
    val showAddMedicalDialog: StateFlow<String?> = _showAddMedicalDialog.asStateFlow()

    private val _showQRCodeDialog = MutableStateFlow<AnimalEntity?>(null)
    val showQRCodeDialog: StateFlow<AnimalEntity?> = _showQRCodeDialog.asStateFlow()

    private val _showWatermarkDialog = MutableStateFlow<Bitmap?>(null)
    val showWatermarkDialog: StateFlow<Bitmap?> = _showWatermarkDialog.asStateFlow()

    private val _showRoleSwitchSheet = MutableStateFlow(false)
    val showRoleSwitchSheet: StateFlow<Boolean> = _showRoleSwitchSheet.asStateFlow()

    // Database Flows
    val allAnimals: StateFlow<List<AnimalEntity>> = repository.allAnimals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGroups: StateFlow<List<HerdGroupEntity>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGestations: StateFlow<List<GestationEntity>> = repository.activeGestations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedicalLogs: StateFlow<List<MedicalLogEntity>> = repository.allMedicalLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Animals
    val filteredAnimals: StateFlow<List<AnimalEntity>> = combine(
        allAnimals,
        _searchQuery,
        _categoryFilter
    ) { animals, query, filter ->
        animals.filter { animal ->
            val matchesQuery = query.isBlank() ||
                animal.tagId.contains(query, ignoreCase = true) ||
                animal.rfid.contains(query, ignoreCase = true) ||
                animal.groupName.contains(query, ignoreCase = true) ||
                animal.category.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "ALL" -> true
                "PREGNANT" -> animal.reproductiveStatus == "pregnant"
                "LACTATING" -> animal.reproductiveStatus == "lactating"
                "LAMBS" -> animal.category.contains("lamb")
                "RAMS" -> animal.category.contains("ram")
                "EWES" -> animal.category.contains("ewe")
                "QUARANTINED" -> animal.isQuarantined
                "LEGACY" -> animal.groupId == "grp_ancient"
                "COHORT_1" -> animal.groupId == "grp_nouveaux_c1"
                "COHORT_2" -> animal.groupId == "grp_nouveaux_c2"
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Valuation Summary
    val valuationSummary: StateFlow<ValuationEngine.AssetValuationSummary> = allAnimals
        .combine(_currentUserRole) { animals, _ ->
            ValuationEngine.compute(animals)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ValuationEngine.compute(emptyList())
        )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    // Navigation and Selection
    fun setScreen(screen: FarmScreen) {
        _currentScreen.value = screen
    }

    fun selectAnimal(tagId: String?) {
        _selectedAnimalTag.value = tagId
        if (tagId != null) {
            _currentScreen.value = FarmScreen.ANIMAL_DETAIL
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(filter: String) {
        _categoryFilter.value = filter
    }

    fun setUserRole(role: UserRole) {
        _currentUserRole.value = role
        _showRoleSwitchSheet.value = false
    }

    fun toggleRoleSwitchSheet(show: Boolean) {
        _showRoleSwitchSheet.value = show
    }

    // Dialog Triggers
    fun setShowAddAnimalDialog(show: Boolean) {
        _showAddAnimalDialog.value = show
    }

    fun setShowAddGestationDialog(show: Boolean) {
        _showAddGestationDialog.value = show
    }

    fun setRecordLambingTarget(eweTag: String?) {
        _showRecordLambingDialog.value = eweTag
    }

    fun setAddMedicalTarget(animalTag: String?) {
        _showAddMedicalDialog.value = animalTag
    }

    fun setShowQRCodeDialog(animal: AnimalEntity?) {
        _showQRCodeDialog.value = animal
    }

    fun setWatermarkedPhoto(bitmap: Bitmap?) {
        _showWatermarkDialog.value = bitmap
    }

    // Operations
    fun addAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            repository.insertAnimal(animal)
            _showAddAnimalDialog.value = false
        }
    }

    fun updateAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            repository.updateAnimal(animal)
        }
    }

    fun deleteAnimal(tagId: String) {
        viewModelScope.launch {
            repository.deleteAnimal(tagId)
            if (_selectedAnimalTag.value == tagId) {
                _selectedAnimalTag.value = null
                _currentScreen.value = FarmScreen.INVENTORY
            }
        }
    }

    fun addGestation(eweTag: String, ramTag: String?, serviceDate: Long, notes: String) {
        viewModelScope.launch {
            repository.addGestation(eweTag, ramTag, serviceDate, notes)
            _showAddGestationDialog.value = false
        }
    }

    fun recordLambing(eweTag: String, deliveryDate: Long, lambs: List<NewbornInfo>) {
        viewModelScope.launch {
            repository.recordLambing(eweTag, deliveryDate, lambs)
            _showRecordLambingDialog.value = null
        }
    }

    fun addMedicalLog(
        animalTag: String,
        treatmentName: String,
        batchNumber: String,
        dosage: String,
        boosterDate: Long?,
        notes: String
    ) {
        viewModelScope.launch {
            val log = MedicalLogEntity(
                id = java.util.UUID.randomUUID().toString(),
                animalTag = animalTag,
                administeredBy = "${_currentUserRole.value.title} (${_currentUserRole.value.name})",
                treatmentName = treatmentName,
                batchNumber = batchNumber,
                dosage = dosage,
                date = System.currentTimeMillis(),
                boosterDate = boosterDate,
                notes = notes
            )
            repository.addMedicalLog(log)
            _showAddMedicalDialog.value = null
        }
    }

    fun sendChatMessage(text: String, tagId: String? = null) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(
                senderName = "${_currentUserRole.value.title} (Field)",
                senderRole = _currentUserRole.value.title,
                text = text,
                tagId = tagId
            )
        }
    }

    fun createAndSendWatermarkedPhoto(tagId: String, notes: String) {
        viewModelScope.launch {
            val basePhoto = WatermarkService.createSampleFieldPhoto(tagId)
            val watermarked = WatermarkService.applyWatermark(
                source = basePhoto,
                meta = WatermarkService.WatermarkMetadata(
                    farmName = "OvinaCloud Sheep Station",
                    workerName = "Staff User",
                    workerRole = _currentUserRole.value.title,
                    tagId = tagId,
                    observationNotes = notes
                )
            )

            repository.sendChatMessage(
                senderName = "Staff User",
                senderRole = _currentUserRole.value.title,
                text = "Attached watermarked biometrics observation for [$tagId]: $notes",
                watermarkText = "Verified Watermark • Tag: $tagId",
                tagId = tagId,
                gpsLocation = "36.7538° N, 3.0588° E"
            )

            _showWatermarkDialog.value = watermarked
        }
    }
}
