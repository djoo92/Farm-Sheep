package com.example.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

object GestationEngine {
    const val SHEEP_GESTATION_PERIOD_DAYS = 150
    const val CRITICAL_ALERT_THRESHOLD_DAYS = 7

    enum class GestationStatus {
        IMMINENT,
        MONITORING,
        SAFE,
        OVERDUE
    }

    data class GestationCalculation(
        val serviceDateMs: Long,
        val expectedDeliveryDateMs: Long,
        val daysRemaining: Int,
        val daysPassed: Int,
        val progressPercent: Int,
        val status: GestationStatus,
        val formattedExpectedDate: String,
        val formattedServiceDate: String
    )

    fun calculate(serviceDateMs: Long, nowMs: Long = System.currentTimeMillis()): GestationCalculation {
        val expectedDeliveryDateMs = serviceDateMs + (SHEEP_GESTATION_PERIOD_DAYS * 86_400_000L)
        val diffMs = expectedDeliveryDateMs - nowMs
        val daysRemaining = (diffMs / 86_400_000L).toInt()
        val daysPassed = SHEEP_GESTATION_PERIOD_DAYS - daysRemaining

        val progress = min(100, max(0, ((daysPassed.toDouble() / SHEEP_GESTATION_PERIOD_DAYS) * 100).toInt()))

        val status = when {
            daysRemaining < 0 -> GestationStatus.OVERDUE
            daysRemaining <= CRITICAL_ALERT_THRESHOLD_DAYS -> GestationStatus.IMMINENT
            daysRemaining <= 30 -> GestationStatus.MONITORING
            else -> GestationStatus.SAFE
        }

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        return GestationCalculation(
            serviceDateMs = serviceDateMs,
            expectedDeliveryDateMs = expectedDeliveryDateMs,
            daysRemaining = daysRemaining,
            daysPassed = max(0, daysPassed),
            progressPercent = progress,
            status = status,
            formattedExpectedDate = dateFormat.format(Date(expectedDeliveryDateMs)),
            formattedServiceDate = dateFormat.format(Date(serviceDateMs))
        )
    }
}
