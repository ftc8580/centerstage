package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.util.isTimedOut

class GoToBottomPosition(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private val runtime = ElapsedTime()
    private val angleChangeTime = 500.0
    private var currentState = GoToBottomPositionState.IDLE

    override fun initialize() {
        currentState = GoToBottomPositionState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            GoToBottomPositionState.STARTED -> {
                deliverySubsystem.setViperExtension(15.0)
                currentState = GoToBottomPositionState.EXTENDING
            }
            GoToBottomPositionState.EXTENDING -> {
                if (deliverySubsystem.isStopped()) {
                    runtime.reset()
                    deliverySubsystem.setAngleLow()
                    currentState = GoToBottomPositionState.LOWERING_ANGLE
                }
            }
            GoToBottomPositionState.LOWERING_ANGLE -> {
                if (runtime.isTimedOut(angleChangeTime)) {
                    deliverySubsystem.setViperPowerAuton(1.0)
                    runtime.reset()
                    currentState = GoToBottomPositionState.RETRACTING
                }
            }
            GoToBottomPositionState.RETRACTING -> {
                if (deliverySubsystem.isRetracted() || runtime.isTimedOut(1000.0)) {
                    deliverySubsystem.setViperBottom()
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
            LOWERING_ANGLE,
            RETRACTING,
            FINISHED
        }
    }
}