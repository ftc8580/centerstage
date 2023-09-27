package org.firstinspires.ftc.teamcode.opmode

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.gamepad.GamepadEx
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry

abstract class OpModeBase : CommandOpMode() {
    lateinit var hardware: HardwareManager
    lateinit var mecanumDrive: SampleMecanumDrive
    lateinit var driverGamepad: GamepadEx
    lateinit var accessoryGamepad: GamepadEx
    lateinit var multitelemetry: MultipleTelemetry

    fun initHardware(isAuto: Boolean) {
        hardware = HardwareManager(CDConfig(), hardwareMap)
        mecanumDrive = SampleMecanumDrive(hardware)
        multitelemetry = MultipleTelemetry(telemetry)
        // Subsystems
        // deposit = Deposit(hardwareMap, isAuto)
        // intake = Intake(hardwareMap)
        // carousel = Carousel(hardwareMap)
        // turretCap = TurretCap(hardwareMap, isAuto)

        // register(intake, deposit, carousel, turretCap)

        driverGamepad = GamepadEx(gamepad1)
        accessoryGamepad = GamepadEx(gamepad2)
    }

    enum class Alliance {
        RED, BLUE;

        fun adjust(input: Double): Double {
            return if (this == RED) input else -input
        }
    }
}