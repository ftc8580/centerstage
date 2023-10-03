package org.firstinspires.ftc.teamcode.config

import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.config.components.DriveMotors

data class CDConfig(
    val driveMotors: DriveMotors = DriveMotors(),
    val viperMotor: String? = "viperMotor",
    val intakeMotor: String? = "intakeMotor",
    val climbMotor: String? = "climbMotor",
    val bucketServo: String? = "bucketServo",
    val deployIntakeServo: String? = "deployIntakeServo",
    val direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD,
    val debugMode: Boolean = false
)
