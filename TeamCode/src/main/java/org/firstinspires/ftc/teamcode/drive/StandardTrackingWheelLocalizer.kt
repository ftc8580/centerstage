package org.firstinspires.ftc.teamcode.drive

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
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
    hardwareMap: HardwareMap,
    private val lastEncPositions: MutableList<Int>,
    private val lastEncVels: MutableList<Int>
) : ThreeTrackingWheelLocalizer(
    listOf(
        Pose2d(0.0, LATERAL_DISTANCE / 2, 0.0),  // left
        Pose2d(0.0, -LATERAL_DISTANCE / 2, 0.0),  // right
        Pose2d(FORWARD_OFFSET, 0.0, Math.toRadians(90.0)) // front
    )
) {
    private val leftEncoder: Encoder
    private val rightEncoder: Encoder
    private val frontEncoder: Encoder

    init {
        leftEncoder = Encoder(
            hardwareMap.get(
                DcMotorEx::class.java, "leftEncoder"
            )
        )
        rightEncoder = Encoder(
            hardwareMap.get(
                DcMotorEx::class.java, "rightEncoder"
            )
        )
        frontEncoder = Encoder(
            hardwareMap.get(
                DcMotorEx::class.java, "frontEncoder"
            )
        )

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
    }

    override fun getWheelPositions(): List<Double> {
        val leftPos = leftEncoder.currentPosition
        val rightPos = rightEncoder.currentPosition
        val frontPos = frontEncoder.currentPosition
        lastEncPositions.clear()
        lastEncPositions.add(leftPos)
        lastEncPositions.add(rightPos)
        lastEncPositions.add(frontPos)
        return listOf(
            encoderTicksToInches(leftPos.toDouble()),
            encoderTicksToInches(rightPos.toDouble()),
            encoderTicksToInches(frontPos.toDouble())
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
            encoderTicksToInches(leftVel.toDouble()),
            encoderTicksToInches(rightVel.toDouble()),
            encoderTicksToInches(frontVel.toDouble())
        )
    }

    companion object {
        var TICKS_PER_REV = 0.0
        var WHEEL_RADIUS = 2.0 // in
        var GEAR_RATIO = 1.0 // output (wheel) speed / input (encoder) speed
        var LATERAL_DISTANCE = 10.0 // in; distance between the left and right wheels
        var FORWARD_OFFSET = 4.0 // in; offset of the lateral wheel
        fun encoderTicksToInches(ticks: Double): Double {
            return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV
        }
    }
}