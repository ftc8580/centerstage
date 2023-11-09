package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class IntakeSubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private val intakeMotor: DcMotorEx
    private val transferMotor: DcMotorEx

    init {
        intakeMotor = hardware.intakeMotor!!
        transferMotor = hardware.transferMotor!!
    }

    fun setIntakePower(power: Double, isReversed: Boolean = false) {
        intakeMotor.power = if (isReversed) -power else power
    }

    fun setTransferPower(power: Double, isReversed: Boolean = false) {
        transferMotor.power = if (isReversed) -power else power
    }

    fun runIntake(power: Double, isReversed: Boolean = false) {
        setIntakePower(power, isReversed)
        setTransferPower(power, isReversed)
    }

    fun runIntake() {
        runIntake(1.0)
    }

    fun runEject() {
        telemetry?.addLine("Ejecting intake with motor power = -1")
        setIntakePower(0.75, true)
    }

    fun stop() {
        telemetry?.addLine("Stopping intake")
        runIntake(0.0)
    }
}