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
                deliverySubsystem.setAngleLow()
                currentState = GoToBottomPositionState.LOWERING_ANGLE
            }
            GoToBottomPositionState.LOWERING_ANGLE -> {
                // Don't bother waiting for the servo, just go
                deliverySubsystem.setViperPower(1.0)
                currentState = GoToBottomPositionState.RETRACTING
            }
            GoToBottomPositionState.RETRACTING -> {
                if (deliverySubsystem.isRetracted()) {
                    deliverySubsystem.setViperPower(0.0)
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
            LOWERING_ANGLE,
            RETRACTING,
            FINISHED
        }
    }
}