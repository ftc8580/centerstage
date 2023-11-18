package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class GoToBottomPosition(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = GoToBottomPositionState.IDLE

    override fun initialize() {
        currentState = GoToBottomPositionState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            GoToBottomPositionState.STARTED -> {
                deliverySubsystem.setViperExtension(10.0)
                currentState = GoToBottomPositionState.EXTENDING
            }
            GoToBottomPositionState.EXTENDING -> {
                if (deliverySubsystem.isStopped()) {
                    deliverySubsystem.setViperPowerAuton(1.0)
                    currentState = GoToBottomPositionState.RETRACTING
                }
            }
            GoToBottomPositionState.RETRACTING -> {
                if (deliverySubsystem.isRetracted()) {
                    deliverySubsystem.setViperPowerAuton(0.0)
                    currentState = GoToBottomPositionState.FINISHED
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == GoToBottomPositionState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        if (interrupted) {
            deliverySubsystem.setViperPower(0.0)
        }

        currentState = GoToBottomPositionState.IDLE
    }

    companion object {
        enum class GoToBottomPositionState {
            IDLE,
            STARTED,
            EXTENDING,
            RETRACTING,
            FINISHED
        }
    }
}