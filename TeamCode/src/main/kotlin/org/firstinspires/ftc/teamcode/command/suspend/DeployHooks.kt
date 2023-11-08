package org.firstinspires.ftc.teamcode.command.suspend

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.SuspendSubsystem

class DeployHooks(private val suspendSubsystem: SuspendSubsystem): CommandBase() {
    init {
        addRequirements(suspendSubsystem)
    }

    override fun initialize() {
        suspendSubsystem.deployHooks()
    }
}