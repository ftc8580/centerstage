package org.firstinspires.ftc.teamcode.opmode

import android.annotation.SuppressLint
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.Subsystem
import com.arcrobotics.ftclib.gamepad.GamepadEx
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.drive.CDMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.subsystem.DroneSubsystem
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem
import org.firstinspires.ftc.teamcode.subsystem.TransferSubsystem
import java.lang.Exception

abstract class OpModeBase : CommandOpMode() {
    lateinit var hardware: HardwareManager
    lateinit var mecanumDrive: CDMecanumDrive
    lateinit var driverGamepad: GamepadEx
    lateinit var accessoryGamepad: GamepadEx
    lateinit var multiTelemetry: MultipleTelemetry

    var deliverySubsystem: DeliverySubsystem? = null
    var droneSubsystem: DroneSubsystem? = null
    var intakeSubsystem: IntakeSubsystem? = null
    var transferSubsystem: TransferSubsystem? = null
//    var suspendSubsystem: SuspendSubsystem? = null
//    var doubleVisionSubsystem: DoubleVision? = null

    @SuppressLint("SdCardPath")
    private val tfliteModelFileName = "/sdcard/FIRST/tflitemodels/CDNewSleeve.tflite"

    fun initHardware(isAuto: Boolean) {
        hardware = HardwareManager(CDConfig(), hardwareMap)
        mecanumDrive = CDMecanumDrive(hardware)
        multiTelemetry = MultipleTelemetry(telemetry)

        // Subsystems
        deliverySubsystem = try { DeliverySubsystem(hardware) } catch (e: Exception) { null }
        droneSubsystem = try { DroneSubsystem(hardware) } catch (e: Exception) { null }
        intakeSubsystem = try { IntakeSubsystem(hardware) } catch (e: Exception) { null }
        transferSubsystem = try { TransferSubsystem(hardware) } catch (e: Exception) { null }
//        suspendSubsystem = try { SuspendSubsystem(hardware) } catch (e: Exception) { null }
//        doubleVisionSubsystem = try {
//            DoubleVision(
//                hardware,
//                multiTelemetry,
//                tfliteModelFileName,
//                listOf(
//                    "1 Pacman",
//                    "2 Cherry",
//                    "3 Ghost"
//                )
//            )
//        } catch (e: Exception) { null }

        val subsystems = listOf<Subsystem?>(
            deliverySubsystem,
            droneSubsystem,
            intakeSubsystem,
//            transferSubsystem,
//            suspendSubsystem
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