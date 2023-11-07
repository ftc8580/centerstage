package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.util.MathUtil
import org.firstinspires.ftc.teamcode.util.ServoUtil
import kotlin.math.abs

class DeliverySubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private var viperMotor: DcMotorEx
    private var bucketServo: Servo
    private var viperAngleServo: Servo
    private var viperBottomPosition: Int
    private var viperTopPosition: Int
    private var viperBottomDecelPosition: Int
    private var viperTopDecelPosition: Int
    private var touchSensor: TouchSensor

    init {
        viperMotor = hardware.viperMotor!!

        bucketServo = hardware.bucketServo!!
        bucketServo.scaleRange(SERVO_SCALE_RANGE_MIN, SERVO_SCALE_RANGE_MAX)
        bucketServo.position = SERVO_START_POSITION

        viperAngleServo = hardware.viperAngleServo!!

        viperBottomPosition = viperMotor.currentPosition
        viperTopPosition = viperBottomPosition - VIPER_RANGE
        viperBottomDecelPosition = viperBottomPosition - VIPER_BACKOFF_RANGE
        viperTopDecelPosition = viperTopPosition + VIPER_BACKOFF_RANGE

        touchSensor = hardware.viperTouch!!
    }

    fun setViperBottom() {
        viperMotor.power = 0.0
        viperBottomPosition = viperMotor.currentPosition
        viperTopPosition = viperBottomPosition - VIPER_RANGE
        viperBottomDecelPosition = viperBottomPosition - VIPER_BACKOFF_RANGE
        viperTopDecelPosition = viperTopPosition + VIPER_BACKOFF_RANGE
    }

    fun isRetracted() = touchSensor.isPressed

    fun closeBucket() {
        bucketServo.position = 0.0
    }

    fun openBucket() {
        bucketServo.position = 1.0
    }

    fun setViperExtension(percent: Double) {
        val extensionDistance = (percent * VIPER_RANGE).toInt()
        val targetPosition = viperBottomPosition - extensionDistance
        setViperPosition(targetPosition)
    }

    fun setViperPosition(target: Int) {
        viperMotor.targetPosition = MathUtil.clamp(viperBottomPosition, viperTopPosition, target)
    }

    fun setViperPower(
        power: Double,
    ) {
        val currentPosition = viperMotor.currentPosition

        telemetry?.addLine("===")
        telemetry?.addLine("viper power: $power")
        telemetry?.addLine("viper bottom pos: $viperBottomPosition")
        telemetry?.addLine("viper top pos: $viperTopPosition")
        telemetry?.addLine("viper curr pos: $currentPosition")

        // Don't continue retracting if at or beyond bottom position
        if (currentPosition >= viperBottomPosition && power > 0) {
            viperMotor.power = 0.0
            return
        }

        // Don't continue extending if at or beyond top position
        if (currentPosition <= viperTopPosition && power < 0) {
            viperMotor.power = 0.0
            return
        }

        // Save some calculations if power is 0
        if (power == 0.0) {
            viperMotor.power = power
        } else {
            val adjustedPower = adjustViperPower(power)

            telemetry?.addLine("viper adj power: $adjustedPower")
            viperMotor.power = adjustedPower
        }

        telemetry?.addLine("===")
    }

    private fun adjustViperPower(power: Double): Double {
        val currentPosition = viperMotor.currentPosition

        return if (currentPosition >= viperBottomDecelPosition && power > 0.0) {
            val remainingRange = abs(viperBottomPosition - currentPosition)
            MathUtil.clamp(-1.0, 1.0, power * (remainingRange / VIPER_BACKOFF_RANGE) + 0.1)
        } else if (currentPosition <= viperTopDecelPosition && power < 0.0) {
            val remainingRange = abs(viperTopPosition - currentPosition)
            MathUtil.clamp(-1.0, 1.0, power * (remainingRange / VIPER_BACKOFF_RANGE) - 0.1)
        } else {
            power
        }
    }

    fun isStopped(): Boolean = !viperMotor.isBusy

    fun getOpenBucketMs(): Double {
        return ServoUtil.getSweepTimeMs(0.0, 1.0, SERVO_SCALE_RANGE_MIN, SERVO_SCALE_RANGE_MAX)
    }

    fun setAngleLow() {
        viperAngleServo.position = VIPER_ANGLE_POSITION_LOW
    }

    fun setAngleHigh() {
        viperAngleServo.position = VIPER_ANGLE_POSITION_HIGH
    }

    companion object {
        // TODO: Check these values
        private const val SERVO_SCALE_RANGE_MIN = 0.5
        private const val SERVO_SCALE_RANGE_MAX = 0.7
        private const val SERVO_START_POSITION = 0.0

        private const val VIPER_RANGE = 3000
        private const val VIPER_BACKOFF_RANGE = 300

        private const val VIPER_ANGLE_POSITION_LOW = 0.18
        private const val VIPER_ANGLE_POSITION_HIGH = 0.25
    }
}