package com.example.domain

import com.example.data.model.AnimalEntity
import java.text.NumberFormat
import java.util.Locale

object ValuationEngine {

    data class CohortValuation(
        val groupName: String,
        val headCount: Int,
        val totalAcquisitionCostDzd: Double,
        val estimatedMarketValuationDzd: Double,
        val averageCostPerHeadDzd: Double
    )

    data class AssetValuationSummary(
        val totalAcquisitionCostDzd: Double,
        val totalEstimatedMarketValuationDzd: Double,
        val totalAnimalsCount: Int,
        val acquiredHeadsCount: Int,
        val averageCostPerAcquiredHeadDzd: Double,
        val cohortBreakdown: List<CohortValuation>,
        val estimatedMonthlyFeedCostDzd: Double,
        val estimatedMonthlyVetCostDzd: Double
    )

    fun compute(animals: List<AnimalEntity>): AssetValuationSummary {
        var totalAcquisition = 0.0
        var totalMarketValue = 0.0
        var acquiredCount = 0

        val grouped = animals.groupBy { it.groupName }
        val cohortList = mutableListOf<CohortValuation>()

        for ((grpName, groupAnimals) in grouped) {
            val count = groupAnimals.size
            val grpCost = groupAnimals.sumOf { it.purchasePriceDzd }
            val grpMarketVal = groupAnimals.sumOf { it.currentValuationDzd }
            val acquiredInGroup = groupAnimals.count { it.purchasePriceDzd > 0 }

            totalAcquisition += grpCost
            totalMarketValue += grpMarketVal
            acquiredCount += acquiredInGroup

            val avg = if (acquiredInGroup > 0) grpCost / acquiredInGroup else 0.0
            cohortList.add(
                CohortValuation(
                    groupName = grpName,
                    headCount = count,
                    totalAcquisitionCostDzd = grpCost,
                    estimatedMarketValuationDzd = grpMarketVal,
                    averageCostPerHeadDzd = avg
                )
            )
        }

        val avgAcquiredCost = if (acquiredCount > 0) totalAcquisition / acquiredCount else 0.0

        // Agricultural feed/vet monthly benchmark in DZD (average 3,200 DZD/head/mo feed + 450 DZD/head/mo vet)
        val monthlyFeed = animals.size * 3200.0
        val monthlyVet = animals.size * 450.0

        return AssetValuationSummary(
            totalAcquisitionCostDzd = totalAcquisition,
            totalEstimatedMarketValuationDzd = totalMarketValue,
            totalAnimalsCount = animals.size,
            acquiredHeadsCount = acquiredCount,
            averageCostPerAcquiredHeadDzd = avgAcquiredCost,
            cohortBreakdown = cohortList,
            estimatedMonthlyFeedCostDzd = monthlyFeed,
            estimatedMonthlyVetCostDzd = monthlyVet
        )
    }

    fun formatDzd(amount: Double): String {
        val format = NumberFormat.getNumberInstance(Locale.FRANCE)
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 2
        return "${format.format(amount)} DZD"
    }
}
