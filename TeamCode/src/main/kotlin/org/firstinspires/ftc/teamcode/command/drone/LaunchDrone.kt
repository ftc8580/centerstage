package org.firstinspires.ftc.teamcode.command.drone

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DroneSubsystem

class LaunchDrone(private val droneSubsystem: DroneSubsystem) : CommandBase() {
    init {
        addRequirements(droneSubsystem)
    }

    private var launchInitialized = false

    override fun execute() {
        droneSubsystem.launch()
        launchInitialized = true
    }

    override fun isFinished(): Boolean = launchInitialized
}