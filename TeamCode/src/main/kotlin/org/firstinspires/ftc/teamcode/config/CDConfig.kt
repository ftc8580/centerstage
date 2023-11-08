package org.firstinspires.ftc.teamcode.config

import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.config.components.DriveMotors

data class CDConfig(
    val driveMotors: DriveMotors = DriveMotors(),
    val viperMotor: String? = "viperMotor",
    val intakeMotor: String? = "intakeMotor",
    val transferMotor: String? = "transferMotor",
    val suspendMotor: String? = "suspendMotor",
    val bucketServo: String? = "bucketServo",
    val viperAngleServo: String? = "viperServo",
    val droneServo: String? = "droneServo",
    val suspendServoLeft: String? = "suspendServoLeft",
    val suspendServoRight: String? = "suspendServoRight",
    val viperPot: String? = "viperPot",
    val viperTouch: String? = "viperTouch",
    val direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD,
    val debugMode: Boolean = false
)
