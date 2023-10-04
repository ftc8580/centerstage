package org.firstinspires.ftc.teamcode.opmode

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.Subsystem
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.subsystem.Delivery
import java.lang.Exception

abstract class OpModeBase : CommandOpMode() {
    lateinit var hardware: HardwareManager
    lateinit var mecanumDrive: SampleMecanumDrive
    lateinit var driverGamepad: GamepadEx
    lateinit var accessoryGamepad: GamepadEx

    abstract var deliverySubsystem: Delivery?

    fun initHardware(isAuto: Boolean) {
        hardware = HardwareManager(CDConfig(), hardwareMap)
        mecanumDrive = SampleMecanumDrive(hardware)

        // Subsystems
        deliverySubsystem = try {
            Delivery(hardware)
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