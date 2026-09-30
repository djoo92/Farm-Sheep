package com.example.data.db

import com.example.data.model.AnimalEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.GestationEntity
import com.example.data.model.HerdGroupEntity
import com.example.data.model.MedicalLogEntity
import java.util.UUID

object SeedData {
    val ONE_DAY_MS = 86_400_000L
    val GESTATION_PERIOD_MS = 150 * ONE_DAY_MS

    fun getInitialGroups(): List<HerdGroupEntity> = listOf(
        HerdGroupEntity(
            id = "grp_ancient",
            name = "Group Ancient",
            description = "Legacy heritage flock - 20 heads (proven maternal bloodlines & pedigree)",
            originType = "legacy_heritage",
            unitPurchaseCostDzd = 0.0,
            initialHeadCount = 20
        ),
        HerdGroupEntity(
            id = "grp_nouveaux_c1",
            name = "Group Nouveaux - Cohort 1",
            description = "Acquisition Batch 1 - 16 purebred ewes @ 175,000 DZD + 14 linked lambs",
            originType = "acquisition",
            unitPurchaseCostDzd = 175000.0,
            initialHeadCount = 30
        ),
        HerdGroupEntity(
            id = "grp_nouveaux_c2",
            name = "Group Nouveaux - Cohort 2",
            description = "Acquisition Batch 2 - 9 select breeding ewes @ 125,000 DZD",
            originType = "acquisition",
            unitPurchaseCostDzd = 125000.0,
            initialHeadCount = 9
        )
    )

    fun getInitialAnimals(): List<AnimalEntity> {
        val now = System.currentTimeMillis()
        val animals = mutableListOf<AnimalEntity>()

        // ==========================================
        // GROUP 1: LEGACY HERD (Total: 20 Heads)
        // ==========================================

        // 1. 4 Primiparous Ewes (First-time gestating, pregnant, due in ~10 days)
        for (i in 1..4) {
            val due = now + (10 * ONE_DAY_MS)
            val service = due - GESTATION_PERIOD_MS
            animals.add(
                AnimalEntity(
                    tagId = "LEG-PRIME-0$i",
                    rfid = "982.000109280$i",
                    groupId = "grp_ancient",
                    groupName = "Group Ancient",
                    category = "primiparous_ewe",
                    gender = "female",
                    birthDate = now - (480 * ONE_DAY_MS),
                    reproductiveStatus = "pregnant",
                    lastServiceDate = service,
                    expectedLambingDate = due,
                    currentWeightKg = 54.0 + (i * 0.5),
                    purchasePriceDzd = 0.0,
                    currentValuationDzd = 160000.0,
                    notes = "First-time gestation. High genetic index for maternal instinct."
                )
            )
        }

        // 2. 1 Breeding Ram (Breeding male)
        animals.add(
            AnimalEntity(
                tagId = "LEG-RAM-01",
                rfid = "982.0001092810",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "breeding_ram",
                gender = "male",
                birthDate = now - (900 * ONE_DAY_MS),
                reproductiveStatus = "serviced",
                currentWeightKg = 86.5,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 220000.0,
                notes = "Primary stud ram (Ouled Djellal lineage). Outstanding conformation."
            )
        )

        // 3. 1 Young Ram (Juvenile male)
        animals.add(
            AnimalEntity(
                tagId = "LEG-JUVRAM-01",
                rfid = "982.0001092811",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "juvenile_ram",
                gender = "male",
                birthDate = now - (210 * ONE_DAY_MS),
                sireTag = "LEG-RAM-01",
                reproductiveStatus = "open",
                currentWeightKg = 43.0,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 95000.0,
                notes = "Future breeding sire candidate. High feed conversion ratio."
            )
        )

        // 4. 4 Mothers with Single Female Lambs (4 mothers + 4 female lambs = 8 heads)
        for (i in 1..4) {
            val motherTag = "LEG-MOTHER-0$i"
            val lambTag = "LEG-LAMB-F-0$i"

            animals.add(
                AnimalEntity(
                    tagId = motherTag,
                    rfid = "982.000109282$i",
                    groupId = "grp_ancient",
                    groupName = "Group Ancient",
                    category = "multiparous_ewe",
                    gender = "female",
                    birthDate = now - (720 * ONE_DAY_MS),
                    reproductiveStatus = "lactating",
                    currentWeightKg = 62.0,
                    purchasePriceDzd = 0.0,
                    currentValuationDzd = 150000.0,
                    notes = "Proven multiparous ewe. Nursing lamb $lambTag."
                )
            )

            animals.add(
                AnimalEntity(
                    tagId = lambTag,
                    rfid = "982.000109283$i",
                    groupId = "grp_ancient",
                    groupName = "Group Ancient",
                    category = "nursing_lamb_female",
                    gender = "female",
                    birthDate = now - (35 * ONE_DAY_MS),
                    damTag = motherTag,
                    sireTag = "LEG-RAM-01",
                    reproductiveStatus = "open",
                    currentWeightKg = 14.5 + (i * 0.4),
                    purchasePriceDzd = 0.0,
                    currentValuationDzd = 45000.0,
                    notes = "Female replacement ewe lamb. Vigorous growth."
                )
            )
        }

        // 5. 1 Mother with Twins (1 Male + 1 Female Lamb = 3 heads)
        val twinMother = "LEG-MOTHER-TWIN"
        animals.add(
            AnimalEntity(
                tagId = twinMother,
                rfid = "982.0001092840",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "multiparous_ewe",
                gender = "female",
                birthDate = now - (750 * ONE_DAY_MS),
                reproductiveStatus = "lactating",
                currentWeightKg = 64.0,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 170000.0,
                notes = "Exceptional prolifacy. Nursing twins."
            )
        )
        animals.add(
            AnimalEntity(
                tagId = "LEG-TWIN-M",
                rfid = "982.0001092841",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "nursing_lamb_male",
                gender = "male",
                birthDate = now - (22 * ONE_DAY_MS),
                damTag = twinMother,
                sireTag = "LEG-RAM-01",
                reproductiveStatus = "open",
                currentWeightKg = 11.2,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 42000.0,
                notes = "Twin male lamb. Excellent vitality."
            )
        )
        animals.add(
            AnimalEntity(
                tagId = "LEG-TWIN-F",
                rfid = "982.0001092842",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "nursing_lamb_female",
                gender = "female",
                birthDate = now - (22 * ONE_DAY_MS),
                damTag = twinMother,
                sireTag = "LEG-RAM-01",
                reproductiveStatus = "open",
                currentWeightKg = 10.5,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 40000.0,
                notes = "Twin female lamb. Nursing well."
            )
        )

        // 6. 1 Mother Born Yesterday (1 mother + 1 newborn male + 1 newborn female = 3 heads)
        val yestMother = "LEG-MOTHER-YEST"
        val yesterday = now - ONE_DAY_MS
        animals.add(
            AnimalEntity(
                tagId = yestMother,
                rfid = "982.0001092850",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "multiparous_ewe",
                gender = "female",
                birthDate = now - (680 * ONE_DAY_MS),
                reproductiveStatus = "lactating",
                currentWeightKg = 59.0,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 165000.0,
                notes = "Delivered healthy twins yesterday. Placenta cleared cleanly."
            )
        )
        animals.add(
            AnimalEntity(
                tagId = "NEWBORN-M-01",
                rfid = "982.0001092851",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "nursing_lamb_male",
                gender = "male",
                birthDate = yesterday,
                damTag = yestMother,
                sireTag = "LEG-RAM-01",
                reproductiveStatus = "open",
                currentWeightKg = 4.3,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 25000.0,
                notes = "Newborn born yesterday. Received 250ml colostrum within 2 hours."
            )
        )
        animals.add(
            AnimalEntity(
                tagId = "NEWBORN-F-01",
                rfid = "982.0001092852",
                groupId = "grp_ancient",
                groupName = "Group Ancient",
                category = "nursing_lamb_female",
                gender = "female",
                birthDate = yesterday,
                damTag = yestMother,
                sireTag = "LEG-RAM-01",
                reproductiveStatus = "open",
                currentWeightKg = 4.0,
                purchasePriceDzd = 0.0,
                currentValuationDzd = 25000.0,
                notes = "Newborn female born yesterday. Navel dipped with 7% iodine."
            )
        )

        // ==========================================
        // GROUP 2: COHORT 1 (16 Ewes @ 175,000 DZD + 14 Lambs = 30 Heads)
        // ==========================================

        // 4 Primiparous Ewes (Pregnant, due in ~18 days)
        for (i in 1..4) {
            val due = now + (18 * ONE_DAY_MS)
            val service = due - GESTATION_PERIOD_MS
            animals.add(
                AnimalEntity(
                    tagId = "C1-PRIME-0$i",
                    rfid = "982.000109300$i",
                    groupId = "grp_nouveaux_c1",
                    groupName = "Group Nouveaux - Cohort 1",
                    category = "primiparous_ewe",
                    gender = "female",
                    birthDate = now - (450 * ONE_DAY_MS),
                    reproductiveStatus = "pregnant",
                    lastServiceDate = service,
                    expectedLambingDate = due,
                    currentWeightKg = 56.5 + (i * 0.5),
                    purchasePriceDzd = 175000.0,
                    currentValuationDzd = 185000.0,
                    notes = "Acquired from certified high-yield breeder. Ultrasound confirmed singleton/twin."
                )
            )
        }

        // 12 Breeding Mothers (Multiparous ewes @ 175,000 DZD)
        for (i in 1..12) {
            val tag = if (i < 10) "C1-MOTHER-0$i" else "C1-MOTHER-$i"
            animals.add(
                AnimalEntity(
                    tagId = tag,
                    rfid = "982.000109301$i",
                    groupId = "grp_nouveaux_c1",
                    groupName = "Group Nouveaux - Cohort 1",
                    category = "multiparous_ewe",
                    gender = "female",
                    birthDate = now - (650 * ONE_DAY_MS),
                    reproductiveStatus = "lactating",
                    currentWeightKg = 61.0 + (i % 5),
                    purchasePriceDzd = 175000.0,
                    currentValuationDzd = 180000.0,
                    notes = "Prime breeding ewe. Acquired under Cohort 1 baseline."
                )
            )
        }

        // 14 Male Lambs linked to Cohort 1
        for (i in 1..14) {
            val tag = if (i < 10) "C1-LAMB-M-0$i" else "C1-LAMB-M-$i"
            animals.add(
                AnimalEntity(
                    tagId = tag,
                    rfid = "982.000109303$i",
                    groupId = "grp_nouveaux_c1",
                    groupName = "Group Nouveaux - Cohort 1",
                    category = "nursing_lamb_male",
                    gender = "male",
                    birthDate = now - (45 * ONE_DAY_MS),
                    reproductiveStatus = "open",
                    currentWeightKg = 16.0 + (i * 0.3),
                    purchasePriceDzd = 0.0,
                    currentValuationDzd = 48000.0,
                    notes = "Cohort 1 linked male lamb. Fattening & growth candidate."
                )
            )
        }

        // ==========================================
        // GROUP 2: COHORT 2 (9 Heads total @ 125,000 DZD)
        // ==========================================
        for (i in 1..9) {
            animals.add(
                AnimalEntity(
                    tagId = "C2-EWE-0$i",
                    rfid = "982.000109400$i",
                    groupId = "grp_nouveaux_c2",
                    groupName = "Group Nouveaux - Cohort 2",
                    category = "multiparous_ewe",
                    gender = "female",
                    birthDate = now - (580 * ONE_DAY_MS),
                    reproductiveStatus = "open",
                    currentWeightKg = 57.0 + (i * 0.7),
                    purchasePriceDzd = 125000.0,
                    currentValuationDzd = 135000.0,
                    notes = "Acquired Cohort 2 ewe. Open condition, conditioning for upcoming breeding cycle."
                )
            )
        }

        return animals
    }

    fun getInitialGestations(): List<GestationEntity> {
        val now = System.currentTimeMillis()
        val list = mutableListOf<GestationEntity>()

        // 4 Legacy Primiparous Ewes (Due in ~10 days)
        for (i in 1..4) {
            val due = now + (10 * ONE_DAY_MS)
            val service = due - GESTATION_PERIOD_MS
            list.add(
                GestationEntity(
                    id = "gest_leg_0$i",
                    eweTag = "LEG-PRIME-0$i",
                    ramTag = "LEG-RAM-01",
                    serviceDate = service,
                    expectedDeliveryDate = due,
                    status = "in_progress",
                    notes = "Day 140/150. Ewe moved to maternal lambing pen. Colostrum test passed."
                )
            )
        }

        // 4 Cohort 1 Primiparous Ewes (Due in ~18 days)
        for (i in 1..4) {
            val due = now + (18 * ONE_DAY_MS)
            val service = due - GESTATION_PERIOD_MS
            list.add(
                GestationEntity(
                    id = "gest_c1_0$i",
                    eweTag = "C1-PRIME-0$i",
                    ramTag = "LEG-RAM-01",
                    serviceDate = service,
                    expectedDeliveryDate = due,
                    status = "in_progress",
                    notes = "Day 132/150. Daily udder development check. Mineral supplementation ongoing."
                )
            )
        }

        return list
    }

    fun getInitialMedicalLogs(): List<MedicalLogEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MedicalLogEntity(
                id = UUID.randomUUID().toString(),
                animalTag = "LEG-PRIME-01",
                administeredBy = "Dr. Benali (Lead Vet)",
                treatmentName = "Enterotoxemia Booster (Covexin 8)",
                batchNumber = "CX-8842-DZ",
                dosage = "2.0 ml Subcutaneous",
                date = now - (14 * ONE_DAY_MS),
                boosterDate = now + (180 * ONE_DAY_MS),
                notes = "Pre-lambing antibody booster for colostral transfer."
            ),
            MedicalLogEntity(
                id = UUID.randomUUID().toString(),
                animalTag = "LEG-RAM-01",
                administeredBy = "Dr. Benali (Lead Vet)",
                treatmentName = "Ivermectin 1% Parasiticide",
                batchNumber = "IVM-2026-9",
                dosage = "1.5 ml Subcutaneous",
                date = now - (30 * ONE_DAY_MS),
                boosterDate = now + (120 * ONE_DAY_MS),
                notes = "Flock deworming program. General body condition 4.5/5."
            ),
            MedicalLogEntity(
                id = UUID.randomUUID().toString(),
                animalTag = "NEWBORN-M-01",
                administeredBy = "Ahmed (Senior Herdsman)",
                treatmentName = "Navel Iodine Dip + Vit E/Selenium",
                batchNumber = "SE-VET-04",
                dosage = "1.0 ml Oral Drench",
                date = now - ONE_DAY_MS,
                boosterDate = null,
                notes = "Post-birth prophylaxis. Strong suckling reflex noted."
            )
        )
    }

    fun getInitialChatMessages(): List<ChatMessageEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                senderName = "Karim (Super Admin)",
                senderRole = "Super Admin",
                messageText = "Biometric census verified. Total herd count: 59 heads across Group Ancient and Group Nouveaux (Cohorts 1 & 2). 8 primiparous ewes now entered critical lambing countdown.",
                timestamp = now - (6 * 3600 * 1000L)
            ),
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                senderName = "Dr. Benali",
                senderRole = "Veterinarian",
                messageText = "Pre-lambing vaccines administered to LEG-PRIME-01 through 04. Electrolytes and heating lamps tested in Nursery Section B.",
                timestamp = now - (4 * 3600 * 1000L)
            ),
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                senderName = "Yassine (Field Worker)",
                senderRole = "Field Worker",
                messageText = "LEG-MOTHER-YEST twins (NEWBORN-M-01 & NEWBORN-F-01) nursing every 2 hours. Bedding refreshed with clean dry oat straw.",
                timestamp = now - (1 * 3600 * 1000L)
            )
        )
    }
}
