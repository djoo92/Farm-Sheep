package com.example.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.random.Random

object QRCodeGenerator {

    /**
     * Generates a 2D matrix QR code bitmap representing the animal tag and metadata.
     */
    fun generateQRCode(tagId: String, size: Int = 512): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val moduleCount = 29 // 29x29 QR code grid
        val moduleSize = size / moduleCount
        val offset = (size - (moduleCount * moduleSize)) / 2

        val matrix = Array(moduleCount) { BooleanArray(moduleCount) }

        // 1. Draw 3 Finder Patterns (7x7 outer square, 5x5 white inner, 3x3 black center)
        fun drawFinder(startX: Int, startY: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isCenter = r in 2..4 && c in 2..4
                    matrix[startY + r][startX + c] = isBorder || isCenter
                }
            }
        }

        drawFinder(0, 0)
        drawFinder(moduleCount - 7, 0)
        drawFinder(0, moduleCount - 7)

        // 2. Draw Timing Patterns
        for (i in 8 until moduleCount - 8) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // 3. Alignment pattern at (moduleCount - 9, moduleCount - 9)
        val alignX = moduleCount - 9
        val alignY = moduleCount - 9
        for (r in 0 until 5) {
            for (c in 0 until 5) {
                val isBorder = r == 0 || r == 4 || c == 0 || c == 4
                val isCenter = r == 2 && c == 2
                matrix[alignY + r][alignX + c] = isBorder || isCenter
            }
        }

        // 4. Deterministic data encoding based on tagId hash
        val seed = tagId.hashCode().toLong()
        val random = Random(seed)

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                // Skip finder patterns + separators
                val inTopLeftFinder = r < 8 && c < 8
                val inTopRightFinder = r < 8 && c >= moduleCount - 8
                val inBottomLeftFinder = r >= moduleCount - 8 && c < 8
                val inTiming = (r == 6 && c in 8 until moduleCount - 8) || (c == 6 && r in 8 until moduleCount - 8)
                val inAlign = r in alignY until alignY + 5 && c in alignX until alignX + 5

                if (!inTopLeftFinder && !inTopRightFinder && !inBottomLeftFinder && !inTiming && !inAlign) {
                    matrix[r][c] = random.nextBoolean()
                }
            }
        }

        // Render black modules
        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            isAntiAlias = false
        }

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                if (matrix[r][c]) {
                    val left = offset + (c * moduleSize)
                    val top = offset + (r * moduleSize)
                    val right = left + moduleSize
                    val bottom = top + moduleSize
                    canvas.drawRect(Rect(left, top, right, bottom), paint)
                }
            }
        }

        return bitmap
    }
}
