package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class IntakeSubsystem(hardware: HardwareManager) : SubsystemBase() {
    private var intakeMotor: DcMotorEx

    init {
        intakeMotor = hardware.intakeMotor!!
    }

    fun runIntake() {
        run(1.0)
    }

    fun runEject() {
        run(-1.0)
    }

    fun stop() {
        intakeMotor.setMotorDisable()
    }

    private fun run(motorPower: Double) {
        intakeMotor.power = motorPower
        intakeMotor.setMotorEnable()
    }
}