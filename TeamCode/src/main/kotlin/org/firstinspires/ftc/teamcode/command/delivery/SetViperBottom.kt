package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class SetViperBottom(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = SetViperBottomState.IDLE

    override fun initialize() {
        currentState = SetViperBottomState.STARTED
    }

    override fun execute() {
        when (currentState) {
            SetViperBottomState.STARTED -> {
                deliverySubsystem.setViperPowerAuton(0.5)
                currentState = SetViperBottomState.SEEKING
            }
            SetViperBottomState.SEEKING -> {
                if (deliverySubsystem.isRetracted()) {
                    deliverySubsystem.setViperBottom()
                    currentState = SetViperBottomState.FINISHED
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == SetViperBottomState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        if (interrupted) {
            deliverySubsystem.setViperPower(0.0)
        }

        currentState = SetViperBottomState.IDLE
    }

    companion object {
        enum class SetViperBottomState {
            IDLE,
            STARTED,
            SEEKING,
            FINISHED
        }
    }
}