package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.Subsystem
import com.arcrobotics.ftclib.gamepad.GamepadEx
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.drive.CDMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import java.lang.Exception

abstract class OpModeBase : CommandOpMode() {
    lateinit var hardware: HardwareManager
    lateinit var mecanumDrive: CDMecanumDrive
    lateinit var driverGamepad: GamepadEx
    lateinit var accessoryGamepad: GamepadEx
    lateinit var multitelemetry: MultipleTelemetry

    private var deliverySubsystem: DeliverySubsystem? = null

    fun initHardware(isAuto: Boolean) {
        hardware = HardwareManager(CDConfig(), hardwareMap)
        mecanumDrive = CDMecanumDrive(hardware)
        multitelemetry = MultipleTelemetry(telemetry)
        // Subsystems
        deliverySubsystem = try {
            DeliverySubsystem(hardware)
        } catch (e: Exception) {
            null
        }

        val subsystems = listOf<Subsystem?>(
            deliverySubsystem
        )

        register(*subsystems.filterNotNull().toTypedArray())

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