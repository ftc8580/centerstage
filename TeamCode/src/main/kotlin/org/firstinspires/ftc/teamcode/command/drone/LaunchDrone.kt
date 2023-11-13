package org.firstinspires.ftc.teamcode.command.drone

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystem.DroneSubsystem
import org.firstinspires.ftc.teamcode.util.isTimedOut

class LaunchDrone(private val droneSubsystem: DroneSubsystem) : CommandBase() {
    private val runtime = ElapsedTime()
    private var currentState = LaunchDroneState.IDLE

    init {
        addRequirements(droneSubsystem)
    }

    override fun initialize() {
        currentState = LaunchDroneState.STARTED
    }

    override fun execute() {
        when (currentState) {
            LaunchDroneState.STARTED -> {
                runtime.reset()
                droneSubsystem.launch()
                currentState = LaunchDroneState.LAUNCHING
            }
            LaunchDroneState.LAUNCHING -> {
                if (runtime.isTimedOut(droneSubsystem.rotationTime)) {
                    currentState = LaunchDroneState.FINISHED
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == LaunchDroneState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        droneSubsystem.reset()
    }

    companion object {
        enum class LaunchDroneState {
            IDLE,
            STARTED,
            LAUNCHING,
            FINISHED
        }
    }
}