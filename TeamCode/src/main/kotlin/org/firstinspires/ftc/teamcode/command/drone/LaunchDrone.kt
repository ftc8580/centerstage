package org.firstinspires.ftc.teamcode.command.drone

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DroneSubsystem

class LaunchDrone(private val droneSubsystem: DroneSubsystem) : CommandBase() {
    init {
        addRequirements(droneSubsystem)
    }

    override fun initialize() {
        droneSubsystem.launch()
    }
}