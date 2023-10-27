package org.firstinspires.ftc.teamcode.command.bucket

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class CloseBucket(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    override fun initialize() {
        deliverySubsystem.openBucket()
    }
}