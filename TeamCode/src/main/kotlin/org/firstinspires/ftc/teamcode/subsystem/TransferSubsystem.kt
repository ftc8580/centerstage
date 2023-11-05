package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class TransferSubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private var intakeMotor: DcMotorEx

    init {
        intakeMotor = hardware.transferMotor!!
    }

    fun runIntake() {
        telemetry?.addLine("Running intake with motor power = 1")
        intakeMotor.power = 1.0
    }

    fun runEject() {
        telemetry?.addLine("Ejecting intake with motor power = -1")
        intakeMotor.power = -1.0
    }

    fun stop() {
        telemetry?.addLine("Stopping intake")
        intakeMotor.power = 0.0
    }
}