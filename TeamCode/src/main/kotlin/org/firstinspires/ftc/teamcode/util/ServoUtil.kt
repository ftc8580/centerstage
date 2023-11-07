package org.firstinspires.ftc.teamcode.util

import kotlin.math.abs

class ServoUtil {
    companion object {
        private const val DEFAULT_SCALE_RANGE_MIN = 0.0
        private const val DEFAULT_SCALE_RANGE_MAX = 1.0
        const val MS_PER_DEGREE = 3.67
        const val DEFAULT_DEGREE_RANGE = 270.0
        const val THRESHOLD_MULTIPLIER = 3.0

        fun getSweepTimeMs(currentPosition: Double, targetPosition: Double): Double {
            return getSweepTimeMs(
                currentPosition,
                targetPosition,
                DEFAULT_SCALE_RANGE_MIN,
                DEFAULT_SCALE_RANGE_MAX
            )
        }

        fun getSweepTimeMs(
            currentPosition: Double,
            targetPosition: Double,
            scaleRangeMin: Double,
            scaleRangeMax: Double,
            degreeRange: Double = DEFAULT_DEGREE_RANGE
        ): Double {
            val positionDelta = abs(currentPosition - targetPosition)
            val scaledDelta = positionDelta * (scaleRangeMax - scaleRangeMin)
            val scaledDegrees = scaledDelta * degreeRange
            return scaledDegrees * MS_PER_DEGREE * THRESHOLD_MULTIPLIER
        }
    }
}