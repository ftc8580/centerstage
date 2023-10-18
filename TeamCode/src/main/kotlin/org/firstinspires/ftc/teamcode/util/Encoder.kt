package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.roadrunner.util.NanoClock
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.roundToInt

/**
 * Wraps a motor instance to provide corrected velocity counts and allow reversing independently of the corresponding
 * slot's motor direction
 */
class Encoder @JvmOverloads constructor(
    private val motor: DcMotorEx,
    private val clock: NanoClock = NanoClock.system()
) {
    enum class Direction(val multiplier: Int) {
        FORWARD(1),
        REVERSE(-1)
    }

    /**
     * Allows you to set the direction of the counts and velocity without modifying the motor's direction state
     */
    var direction = Direction.FORWARD

    private var lastPosition: Int = 0
    private var velocityEstimateIdx = 0
    private val velocityEstimates = DoubleArray(3)
    private var lastUpdateTime: Double = clock.seconds()

    private val multiplier: Int
        get() = direction.multiplier * if (motor.direction == DcMotorSimple.Direction.FORWARD) 1 else -1

    /**
     * Gets the position from the underlying motor and adjusts for the set direction.
     * Additionally, this method updates the velocity estimates used for compensated velocity
     *
     * @return encoder position
     */
    val currentPosition: Int
        get() {
            val multiplier = multiplier
            val currentPosition = motor.currentPosition * multiplier
            if (currentPosition != lastPosition) {
                val currentTime = clock.seconds()
                val dt = currentTime - lastUpdateTime
                velocityEstimates[velocityEstimateIdx] = (currentPosition - lastPosition) / dt
                velocityEstimateIdx = (velocityEstimateIdx + 1) % 3
                lastPosition = currentPosition
                lastUpdateTime = currentTime
            }
            return currentPosition
        }

    /**
     * Gets the velocity directly from the underlying motor and compensates for the direction
     * See [.getCorrectedVelocity] for high (>2^15) counts per second velocities (such as on REV Through Bore)
     *
     * @return raw velocity
     */
    private val rawVelocity: Double
        get() {
            val multiplier = multiplier
            return motor.velocity * multiplier
        }

    /**
     * Uses velocity estimates gathered in [.getCurrentPosition] to estimate the upper bits of velocity
     * that are lost in overflow due to velocity being transmitted as 16 bits.
     * CAVEAT: must regularly call [.getCurrentPosition] for the compensation to work correctly.
     *
     * @return corrected velocity
     */
    val correctedVelocity: Double
        get() {
            val median = if (velocityEstimates[0] > velocityEstimates[1]) velocityEstimates[1].coerceAtLeast(
                velocityEstimates[0].coerceAtMost(velocityEstimates[2])
            ) else velocityEstimates[0].coerceAtLeast(
                velocityEstimates[1].coerceAtMost(velocityEstimates[2])
            )
            return inverseOverflow(rawVelocity, median)
        }

    companion object {
        private const val CPS_STEP = 0x10000
        private fun inverseOverflow(input: Double, estimate: Double): Double {
            // convert to uint16
            var real = input.toInt() and 0xffff
            // initial, modulo-based correction: it can recover the remainder of 5 of the upper 16 bits
            // because the velocity is always a multiple of 20 cps due to Expansion Hub's 50ms measurement window
            real += real % 20 / 4 * CPS_STEP
            // estimate-based correction: it finds the nearest multiple of 5 to correct the upper bits by
            real += (((estimate - real) / (5 * CPS_STEP)).roundToInt() * 5 * CPS_STEP)
            return real.toDouble()
        }
    }
}