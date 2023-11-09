package org.firstinspires.ftc.teamcode.util

import kotlin.math.roundToInt

class MathUtil {
    companion object {
        fun isWithinRange(lowerBound: Double, upperBound: Double, target: Double): Boolean {
            return target in lowerBound..upperBound
        }

        fun isWithinRange(lowerBound: Int, upperBound: Int, target: Int): Boolean {
            return target in lowerBound..upperBound
        }

        fun clamp(min: Double, max: Double, input: Double): Double {
            return max.coerceAtMost(min.coerceAtLeast(input))
        }

        fun clamp(min: Int, max: Int, input: Int): Int {
            val correctedMin = if (min > max) max else min
            val correctedMax = if (min > max) min else max
            return correctedMax.coerceAtMost(correctedMin.coerceAtLeast(input))
        }

        fun round(input: Double): Double {
            return (input * 1000.0).roundToInt() / 1000.0
        }
    }
}