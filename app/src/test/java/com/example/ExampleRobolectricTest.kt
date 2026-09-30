package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.SeedData
import com.example.domain.GestationEngine
import com.example.domain.ValuationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OvinaCloud", appName)
    }

    @Test
    fun `verify initial seed data counts match exact specifications`() {
        val animals = SeedData.getInitialAnimals()
        // 20 in Legacy Heritage + 30 in Cohort 1 + 9 in Cohort 2 = 59 total heads
        assertEquals(59, animals.size)

        val legacyCount = animals.count { it.groupId == "grp_ancient" }
        assertEquals(20, legacyCount)

        val cohort1Count = animals.count { it.groupId == "grp_nouveaux_c1" }
        assertEquals(30, cohort1Count)

        val cohort2Count = animals.count { it.groupId == "grp_nouveaux_c2" }
        assertEquals(9, cohort2Count)

        val pregnantCount = animals.count { it.reproductiveStatus == "pregnant" }
        assertEquals(8, pregnantCount) // 4 in Legacy + 4 in Cohort 1
    }

    @Test
    fun `verify 150-day biological gestation engine calculations`() {
        val now = System.currentTimeMillis()
        val serviceDate = now - (140L * 86_400_000L) // 140 days ago -> 10 days remaining
        val calc = GestationEngine.calculate(serviceDate, now)

        assertEquals(10, calc.daysRemaining)
        assertEquals(140, calc.daysPassed)
        assertTrue(calc.progressPercent >= 93)
        assertEquals(GestationEngine.GestationStatus.MONITORING, calc.status)
    }

    @Test
    fun `verify herd asset valuation computation`() {
        val animals = SeedData.getInitialAnimals()
        val valuation = ValuationEngine.compute(animals)

        // 16 purchased ewes @ 175,000 + 9 ewes @ 125,000 = 2,800,000 + 1,125,000 = 3,925,000 DZD
        assertEquals(3925000.0, valuation.totalAcquisitionCostDzd, 0.01)
        assertEquals(25, valuation.acquiredHeadsCount)
        assertEquals(157000.0, valuation.averageCostPerAcquiredHeadDzd, 0.01)
    }
}
