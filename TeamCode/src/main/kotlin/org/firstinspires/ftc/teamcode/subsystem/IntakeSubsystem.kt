package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class IntakeSubsystem(hardware: HardwareManager) : SubsystemBase() {
    private val intakeMotor: DcMotorEx

    init {
        intakeMotor = hardware.intakeMotor!!
    }

    fun moveTo(position: Double) {
        TODO()
    }
}