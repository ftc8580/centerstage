package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class DeliveryPositionOne(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = DeliveryPositionOneState.IDLE

    override fun initialize() {
        currentState = DeliveryPositionOneState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            DeliveryPositionOneState.STARTED -> {
                deliverySubsystem.setViperExtension(33.3)
                currentState = DeliveryPositionOneState.EXTENDING
            }
            DeliveryPositionOneState.EXTENDING -> {
                if (deliverySubsystem.isStopped()) {
                    currentState = DeliveryPositionOneState.FINISHED
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == DeliveryPositionOneState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        if (interrupted) {
            deliverySubsystem.setViperPower(0.0)
        }

        currentState = DeliveryPositionOneState.IDLE
    }

    companion object {
        enum class DeliveryPositionOneState {
            IDLE,
            STARTED,
            EXTENDING,
            FINISHED
        }
    }
}