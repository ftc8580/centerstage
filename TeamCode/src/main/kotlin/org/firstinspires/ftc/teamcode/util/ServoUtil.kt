package org.firstinspires.ftc.teamcode.util

class ServoUtil {
    companion object {
        private const val MS_PER_DEGREE = 3.67
        private const val DEFAULT_DEGREE_RANGE = 270.0
        private const val DEFAULT_SCALE_RANGE_MIN = 0.0
        private const val DEFAULT_SCALE_RANGE_MAX = 1.0
        private const val THRESHOLD_MULTIPLIER = 3.0

        fun getSweepTimeMs(currentPosition: Double, targetPosition: Double): Double {
            return getSweepTimeMs(
                currentPosition,
                targetPosition,
                DEFAULT_SCALE_RANGE_MIN,
                DEFAULT_SCALE_RANGE_MAX,
                DEFAULT_DEGREE_RANGE
            )
        }

        fun getSweepTimeMs(
            currentPosition: Double,
            targetPosition: Double,
            scaleRangeMin: Double,
            scaleRangeMax: Double
        ): Double {
            return getSweepTimeMs(
                currentPosition,
                targetPosition,
                scaleRangeMin,
                scaleRangeMax,
                DEFAULT_DEGREE_RANGE
            )
        }

        private fun getSweepTimeMs(
            currentPosition: Double,
            targetPosition: Double,
            scaleRangeMin: Double,
            scaleRangeMax: Double,
            degreeRange: Double
        ): Double {
            val positionDelta = Math.abs(currentPosition - targetPosition)
            val scaledDelta = positionDelta * (scaleRangeMax - scaleRangeMin)
            val scaledDegrees = scaledDelta * degreeRange
            return scaledDegrees * MS_PER_DEGREE * THRESHOLD_MULTIPLIER
        }
    }
}