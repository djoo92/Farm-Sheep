package com.example.data.repository

import com.example.data.db.OvinaDatabase
import com.example.data.db.SeedData
import com.example.data.model.AnimalEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.GestationEntity
import com.example.data.model.HerdGroupEntity
import com.example.data.model.MedicalLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class FarmRepository(private val database: OvinaDatabase) {
    private val animalDao = database.animalDao()
    private val herdGroupDao = database.herdGroupDao()
    private val gestationDao = database.gestationDao()
    private val medicalDao = database.medicalDao()
    private val chatDao = database.chatDao()

    val allAnimals: Flow<List<AnimalEntity>> = animalDao.getAllAnimals()
    val allGroups: Flow<List<HerdGroupEntity>> = herdGroupDao.getAllGroups()
    val activeGestations: Flow<List<GestationEntity>> = gestationDao.getActiveGestations()
    val allGestations: Flow<List<GestationEntity>> = gestationDao.getAllGestations()
    val allMedicalLogs: Flow<List<MedicalLogEntity>> = medicalDao.getAllLogs()
    val chatMessages: Flow<List<ChatMessageEntity>> = chatDao.getMessages()

    val totalCount: Flow<Int> = animalDao.getTotalCount()
    val pregnantCount: Flow<Int> = animalDao.getPregnantCount()
    val lambsCount: Flow<Int> = animalDao.getLambsCount()
    val ramsCount: Flow<Int> = animalDao.getRamsCount()
    val ewesCount: Flow<Int> = animalDao.getEwesCount()

    fun getAnimal(tagId: String): Flow<AnimalEntity?> = animalDao.getAnimalByTag(tagId)

    fun getOffspring(damTag: String): Flow<List<AnimalEntity>> = animalDao.getOffspring(damTag)

    fun getMedicalLogsForAnimal(tagId: String): Flow<List<MedicalLogEntity>> = medicalDao.getLogsForAnimal(tagId)

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val existing = animalDao.getAllAnimals().first()
        if (existing.isEmpty()) {
            herdGroupDao.insertAll(SeedData.getInitialGroups())
            animalDao.insertAll(SeedData.getInitialAnimals())
            gestationDao.insertAll(SeedData.getInitialGestations())
            medicalDao.insertAll(SeedData.getInitialMedicalLogs())
            chatDao.insertAll(SeedData.getInitialChatMessages())
        }
    }

    suspend fun insertAnimal(animal: AnimalEntity) = withContext(Dispatchers.IO) {
        animalDao.insertAnimal(animal)
    }

    suspend fun updateAnimal(animal: AnimalEntity) = withContext(Dispatchers.IO) {
        animalDao.updateAnimal(animal)
    }

    suspend fun deleteAnimal(tagId: String) = withContext(Dispatchers.IO) {
        animalDao.deleteByTag(tagId)
    }

    suspend fun addGestation(
        eweTag: String,
        ramTag: String?,
        serviceDate: Long,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val expectedDelivery = serviceDate + (150L * 86_400_000L)
        val gestation = GestationEntity(
            id = UUID.randomUUID().toString(),
            eweTag = eweTag,
            ramTag = ramTag,
            serviceDate = serviceDate,
            expectedDeliveryDate = expectedDelivery,
            status = "in_progress",
            notes = notes
        )
        gestationDao.insertGestation(gestation)

        // Update ewe reproductive status
        val ewe = animalDao.getAnimalByTagOnce(eweTag)
        if (ewe != null) {
            animalDao.updateAnimal(
                ewe.copy(
                    reproductiveStatus = "pregnant",
                    lastServiceDate = serviceDate,
                    expectedLambingDate = expectedDelivery,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun recordLambing(
        eweTag: String,
        deliveryDate: Long,
        lambs: List<NewbornInfo>
    ) = withContext(Dispatchers.IO) {
        // Update active gestation to delivered
        val activeGests = gestationDao.getActiveGestations().first()
        val gest = activeGests.find { it.eweTag == eweTag }
        if (gest != null) {
            gestationDao.updateGestation(
                gest.copy(
                    actualDeliveryDate = deliveryDate,
                    offspringCount = lambs.size,
                    status = "delivered"
                )
            )
        }

        // Update mother ewe
        val ewe = animalDao.getAnimalByTagOnce(eweTag)
        if (ewe != null) {
            animalDao.updateAnimal(
                ewe.copy(
                    reproductiveStatus = "lactating",
                    expectedLambingDate = null,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        // Insert new lambs
        for (info in lambs) {
            val lambEntity = AnimalEntity(
                tagId = info.tagId,
                rfid = info.rfid,
                groupId = ewe?.groupId ?: "grp_ancient",
                groupName = ewe?.groupName ?: "Group Ancient",
                category = if (info.gender == "male") "nursing_lamb_male" else "nursing_lamb_female",
                gender = info.gender,
                birthDate = deliveryDate,
                damTag = eweTag,
                sireTag = gest?.ramTag ?: "LEG-RAM-01",
                reproductiveStatus = "open",
                currentWeightKg = info.weightKg,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 30000.0,
                notes = "Born to dam $eweTag. Vigorous."
            )
            animalDao.insertAnimal(lambEntity)
        }
    }

    suspend fun addMedicalLog(log: MedicalLogEntity) = withContext(Dispatchers.IO) {
        medicalDao.insertLog(log)
    }

    suspend fun sendChatMessage(
        senderName: String,
        senderRole: String,
        text: String,
        mediaUri: String? = null,
        watermarkText: String? = null,
        tagId: String? = null,
        gpsLocation: String? = null
    ) = withContext(Dispatchers.IO) {
        val msg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            senderName = senderName,
            senderRole = senderRole,
            messageText = text,
            mediaUri = mediaUri,
            watermarkText = watermarkText,
            tagId = tagId,
            gpsLocation = gpsLocation,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(msg)
    }
}

data class NewbornInfo(
    val tagId: String,
    val rfid: String = "",
    val gender: String, // "male", "female"
    val weightKg: Double = 4.0
)
