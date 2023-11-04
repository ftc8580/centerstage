package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.util.MathUtil
import org.firstinspires.ftc.teamcode.util.PIDController
import org.firstinspires.ftc.teamcode.util.ServoUtil
import kotlin.math.abs

class DeliverySubsystem(hardware: HardwareManager) : SubsystemBase() {
    private var viperMotor: DcMotorEx
    private var bucketServo: Servo

    init {
        viperMotor = hardware.viperMotor!!
        bucketServo = hardware.bucketServo!!

        bucketServo.scaleRange(SERVO_SCALE_RANGE_MIN, SERVO_SCALE_RANGE_MAX)
        bucketServo.position = SERVO_START_POSITION
    }

    fun closeBucket() {
        bucketServo.position = 0.0
    }

    fun openBucket() {
        bucketServo.position = 1.0
    }

    fun setViperExtension(percent: Double) {
        val extensionDistance = (percent * viperRangeDistance).toInt()
        val targetPosition = VIPER_RANGE_MIN - extensionDistance
        setViperPosition(targetPosition)
    }

    fun setViperPosition(target: Int) {
        viperMotor.targetPosition = MathUtil.clamp(VIPER_RANGE_MIN, VIPER_RANGE_MAX, target)
    }

    fun setViperPower(
        power: Double,
        pidController: PIDController? = null
    ) {
        val currentPosition = viperMotor.currentPosition
        var targetPosition = 0.0

        if (currentPosition >= VIPER_RANGE_MIN || currentPosition <= VIPER_RANGE_MAX) {
            viperMotor.power = 0.0
            return
        }

        if (power > 0) {
            targetPosition = VIPER_RANGE_MAX.toDouble()
        } else if (power < 0) {
            targetPosition = VIPER_RANGE_MIN.toDouble()
        }

        if (power == 0.0 || pidController == null) {
            viperMotor.power = power
        } else {
            val controlSignal = pidController.calculateControlSignal(currentPosition.toDouble(), targetPosition)
            val powerMultiplier = controlSignal / viperRangeDistance
            viperMotor.power = power * powerMultiplier
        }
    }

    fun isStopped(): Boolean = true // !viperMotor.isBusy

    fun getOpenBucketMs(): Double {
        return ServoUtil.getSweepTimeMs(0.0, 1.0, SERVO_SCALE_RANGE_MIN, SERVO_SCALE_RANGE_MAX)
    }

    companion object {
        // TODO: Check these values
        private const val SERVO_SCALE_RANGE_MIN = 0.5
        private const val SERVO_SCALE_RANGE_MAX = 0.7
        private const val SERVO_START_POSITION = 0.0

        private const val VIPER_RANGE_MIN = 667
        private const val VIPER_RANGE_MAX = -2308

        private val viperRangeDistance = abs(VIPER_RANGE_MAX - VIPER_RANGE_MIN)
    }
}