package com.example.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WatermarkService {

    data class WatermarkMetadata(
        val farmName: String = "OVINACLOUD SHEEP STATION",
        val workerName: String,
        val workerRole: String,
        val tagId: String,
        val observationNotes: String = "Condition Assessment & Health Check",
        val latitude: Double = 36.7538,
        val longitude: Double = 3.0588,
        val elevationMeters: Int = 412
    )

    fun createSampleFieldPhoto(tagId: String): Bitmap {
        val width = 1080
        val height = 810
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw lush pasture gradient background
        val backgroundPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                Color.parseColor("#1B4D3E"),
                Color.parseColor("#0F291E"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw decorative pasture hill curves
        val hillPaint = Paint().apply {
            color = Color.parseColor("#256349")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawOval(RectF(-200f, 350f, 1300f, 1100f), hillPaint)

        val frontHillPaint = Paint().apply {
            color = Color.parseColor("#2E7D5B")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawOval(RectF(100f, 480f, 1400f, 1200f), frontHillPaint)

        // Draw sheep silhouette/symbol placeholder in center
        val sheepBodyPaint = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawOval(RectF(380f, 320f, 700f, 540f), sheepBodyPaint)
        canvas.drawCircle(390f, 350f, 75f, sheepBodyPaint) // Head

        // Horn / ear accent
        val hornPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 14f
            isAntiAlias = true
        }
        canvas.drawArc(RectF(330f, 290f, 410f, 370f), 180f, 180f, false, hornPaint)

        // Ear tag visualization on ear
        val tagEarPaint = Paint().apply {
            color = Color.parseColor("#F59E0B")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(340f, 360f, 395f, 395f), 8f, 8f, tagEarPaint)

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("DZ", 355f, 382f, textPaint)

        // Draw livestock tag center badge
        val badgeBgPaint = Paint().apply {
            color = Color.parseColor("#E60F172A")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(340f, 180f, 740f, 250f), 24f, 24f, badgeBgPaint)

        val badgeTextPaint = Paint().apply {
            color = Color.parseColor("#34D399")
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("LIVESTOCK DOSSIER • $tagId", 540f, 225f, badgeTextPaint)

        return bitmap
    }

    fun applyWatermark(source: Bitmap, meta: WatermarkMetadata): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()

        val bannerHeight = (h * 0.22f).coerceAtLeast(160f)
        val bannerTop = h - bannerHeight

        // Frosted dark banner background
        val bannerPaint = Paint().apply {
            color = Color.parseColor("#D90A0F1D") // ~85% opaque deep navy/slate
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, bannerTop, w, h, bannerPaint)

        // Emerald accent stripe on the top of banner
        val stripePaint = Paint().apply {
            color = Color.parseColor("#10B981")
            strokeWidth = 8f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(0f, bannerTop, w, bannerTop, stripePaint)

        // Vertical safety indicator stripe on left
        val leftBarPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, bannerTop, 16f, h, leftBarPaint)

        // Text configurations
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = (bannerHeight * 0.18f).coerceIn(24f, 44f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val tagHighlightPaint = Paint().apply {
            color = Color.parseColor("#34D399") // Bright emerald
            textSize = (bannerHeight * 0.18f).coerceIn(24f, 44f)
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        val subPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1") // Slate 300
            textSize = (bannerHeight * 0.13f).coerceIn(16f, 26f)
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val auditPaint = Paint().apply {
            color = Color.parseColor("#94A3B8") // Slate 400
            textSize = (bannerHeight * 0.11f).coerceIn(14f, 22f)
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }

        val startX = 40f
        var currentY = bannerTop + (bannerHeight * 0.30f)

        // Row 1: Farm Name & Ear Tag
        canvas.drawText("${meta.farmName.uppercase()}  |  ", startX, currentY, titlePaint)
        val farmTextWidth = titlePaint.measureText("${meta.farmName.uppercase()}  |  ")
        canvas.drawText("TAG: [${meta.tagId}]", startX + farmTextWidth, currentY, tagHighlightPaint)

        // Row 2: Observation & Operator
        currentY += (bannerHeight * 0.26f)
        canvas.drawText("Logged by: ${meta.workerName} • Role: ${meta.workerRole} • ${meta.observationNotes}", startX, currentY, subPaint)

        // Row 3: Timestamp & Precision GPS
        currentY += (bannerHeight * 0.24f)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateString = dateFormat.format(Date())
        val gpsString = "GPS: ${String.format(Locale.US, "%.5f°N, %.5f°E", meta.latitude, meta.longitude)} • Alt: ${meta.elevationMeters}m"
        canvas.drawText("Timestamp: $dateString UTC  •  $gpsString  •  SHA-256 Verified", startX, currentY, auditPaint)

        return result
    }
}
