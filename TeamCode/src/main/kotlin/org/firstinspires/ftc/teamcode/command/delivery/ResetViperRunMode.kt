package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class ResetViperRunMode(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var runModeSet = false

    override fun initialize() {
        deliverySubsystem.setViperPower(0.0)
        deliverySubsystem.viperRunMode(DcMotor.RunMode.RUN_USING_ENCODER)
        runModeSet = true
    }

    override fun isFinished(): Boolean {
        return runModeSet
    }
}