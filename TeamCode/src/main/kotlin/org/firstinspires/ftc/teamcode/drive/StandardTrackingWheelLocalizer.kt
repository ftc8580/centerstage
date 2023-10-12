package org.firstinspires.ftc.teamcode.drive

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.util.Encoder

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 */
@Config
class StandardTrackingWheelLocalizer(
    hardware: HardwareManager,
    private val lastEncPositions: MutableList<Int>,
    private val lastEncVels: MutableList<Int>
) : ThreeTrackingWheelLocalizer(
    listOf(
        Pose2d(0.0, LATERAL_DISTANCE / 2, 0.0),  // left
        Pose2d(0.0, -LATERAL_DISTANCE / 2, 0.0),  // right
        Pose2d(FORWARD_OFFSET, 0.0, Math.toRadians(90.0)) // front
    )
) {
    private val leftEncoder: Encoder = hardware.leftEncoder!!
    private val rightEncoder: Encoder = hardware.rightEncoder!!
    private val frontEncoder: Encoder = hardware.frontEncoder!!

    private var xMultiplier = 1.0348634
    private var yMultiplier = 1.05815129

    override fun getWheelPositions(): List<Double> {
        val leftPos = leftEncoder.currentPosition
        val rightPos = rightEncoder.currentPosition
        val frontPos = frontEncoder.currentPosition
        lastEncPositions.clear()
        lastEncPositions.add(leftPos)
        lastEncPositions.add(rightPos)
        lastEncPositions.add(frontPos)
        return listOf(
            encoderTicksToInches(leftPos.toDouble()) * xMultiplier,
            encoderTicksToInches(rightPos.toDouble()) * xMultiplier,
            encoderTicksToInches(frontPos.toDouble()) * yMultiplier
        )
    }

    override fun getWheelVelocities(): List<Double> {
        val leftVel = leftEncoder.correctedVelocity.toInt()
        val rightVel = rightEncoder.correctedVelocity.toInt()
        val frontVel = frontEncoder.correctedVelocity.toInt()
        lastEncVels.clear()
        lastEncVels.add(leftVel)
        lastEncVels.add(rightVel)
        lastEncVels.add(frontVel)
        return listOf(
            encoderTicksToInches(leftVel.toDouble()) * xMultiplier,
            encoderTicksToInches(rightVel.toDouble()) * xMultiplier,
            encoderTicksToInches(frontVel.toDouble()) * yMultiplier
        )
    }

    companion object {
        var TICKS_PER_REV = 8192.0
        var WHEEL_RADIUS = 0.6889764 // in
        var GEAR_RATIO = 1.0 // output (wheel) speed / input (encoder) speed
        var LATERAL_DISTANCE = 12.3 // in; distance between the left and right wheels
        var FORWARD_OFFSET = -6.5 // in; offset of the lateral wheel
        fun encoderTicksToInches(ticks: Double): Double {
            return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV
        }
    }
}