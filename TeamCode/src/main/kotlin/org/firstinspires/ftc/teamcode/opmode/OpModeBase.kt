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
import org.firstinspires.ftc.teamcode.subsystem.SuspendSubsystem
import org.firstinspires.ftc.teamcode.vision.TensorFlowObjectDetection
import java.lang.Exception

abstract class OpModeBase : CommandOpMode() {
    lateinit var hardware: HardwareManager
    lateinit var mecanumDrive: CDMecanumDrive
    lateinit var driverGamepad: GamepadEx
    lateinit var accessoryGamepad: GamepadEx
    lateinit var multiTelemetry: MultipleTelemetry

    // Subsystems
    var deliverySubsystem: DeliverySubsystem? = null
    var droneSubsystem: DroneSubsystem? = null
    var intakeSubsystem: IntakeSubsystem? = null
    var suspendSubsystem: SuspendSubsystem? = null

    // Vision
    var tfod: TensorFlowObjectDetection? = null

    @SuppressLint("SdCardPath")
    private val tfliteModelFileName = "/sdcard/FIRST/tflitemodels/CDCenterStage.tflite"

    fun initHardware(isAuto: Boolean) {
        hardware = HardwareManager(CDConfig(), hardwareMap)
        mecanumDrive = CDMecanumDrive(hardware)
        multiTelemetry = MultipleTelemetry(telemetry)

        // Subsystems
        deliverySubsystem = try { DeliverySubsystem(hardware, multiTelemetry) } catch (e: Exception) { null }
        droneSubsystem = try { DroneSubsystem(hardware, multiTelemetry) } catch (e: Exception) { null }
        intakeSubsystem = try { IntakeSubsystem(hardware, multiTelemetry) } catch (e: Exception) { null }
        suspendSubsystem = try { SuspendSubsystem(hardware, multiTelemetry) } catch (e: Exception) { null }

        val subsystems = listOf<Subsystem?>(
            deliverySubsystem,
            droneSubsystem,
            intakeSubsystem,
            suspendSubsystem
        )

        register(*subsystems.filterNotNull().toTypedArray())

        // Vision
        if (isAuto) {
            tfod = TensorFlowObjectDetection(
                hardware,
                multiTelemetry,
                tfliteModelFileName,
                listOf("Blue_Prop", "Red_Prop")
            )
        }

        // Game pads
        driverGamepad = GamepadEx(gamepad1)
        accessoryGamepad = GamepadEx(gamepad2)
    }

    enum class Alliance {
        RED, BLUE;

        fun adjust(input: Double): Double {
            return if (this == BLUE) input else -input
        }
    }
}