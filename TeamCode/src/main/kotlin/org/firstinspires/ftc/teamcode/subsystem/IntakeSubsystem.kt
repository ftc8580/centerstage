package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class IntakeSubsystem(hardware: HardwareManager) : SubsystemBase() {
    private var intakeMotor: DcMotorEx

    init {
        intakeMotor = hardware.intakeMotor!!
    }

    fun runForward() {
        intakeMotor.power = 1.0
        intakeMotor.setMotorEnable()
    }

    fun runReverse() {
        intakeMotor.power = -1.0
        intakeMotor.setMotorEnable()
    }

    fun stop() {
        intakeMotor.setMotorDisable()
    }
}