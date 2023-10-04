package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class Delivery(hardware: HardwareManager) : SubsystemBase() {
    private var viperMotor: DcMotorEx
    private var bucketServo: Servo

    init {
        viperMotor = hardware.viperMotor!!
        bucketServo = hardware.bucketServo!!

        bucketServo.scaleRange(SERVO_SCALE_RANGE_MIN, SERVO_SCALE_RANGE_MAX)
        bucketServo.position = SERVO_START_POSITION
    }

    fun getBucketServoPosition(): Double = bucketServo.position
    fun setBucketServoPosition(position: Double) {
        bucketServo.position = position
    }

    companion object {
        // TODO: Check these values
        private const val SERVO_SCALE_RANGE_MIN = 0.5
        private const val SERVO_SCALE_RANGE_MAX = 0.9
        private const val SERVO_START_POSITION = 0.0
    }
}