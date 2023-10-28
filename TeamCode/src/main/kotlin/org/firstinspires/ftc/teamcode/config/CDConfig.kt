package org.firstinspires.ftc.teamcode.config

import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.config.components.DriveMotors

data class CDConfig(
    val driveMotors: DriveMotors = DriveMotors(),
    val viperMotor: String? = "viperMotor",
    val intakeMotor: String? = "intakeMotor",
    val transferMotor: String? = "transferMotor",
    val climbMotor: String? = "climbMotor",
    val bucketServo: String? = "bucketServo",
    val viperAngleServo: String? = "viperAngle",
    val droneServo: String? = "droneServo",
    val viperPot: String? = "viperPot",
    val direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD,
    val debugMode: Boolean = false
)
