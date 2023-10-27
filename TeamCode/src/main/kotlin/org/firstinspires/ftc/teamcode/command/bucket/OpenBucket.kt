package org.firstinspires.ftc.teamcode.command.bucket

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class OpenBucket(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    override fun initialize() {
        deliverySubsystem.openBucket()
    }
}