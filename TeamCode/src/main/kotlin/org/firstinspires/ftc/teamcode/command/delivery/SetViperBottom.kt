package org.firstinspires.ftc.teamcode.command.delivery

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem

class SetViperBottom(private val deliverySubsystem: DeliverySubsystem, private val telemetry: MultipleTelemetry) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = SetViperBottomState.IDLE

    override fun initialize() {
        telemetry.addLine("Intializing ${this.name}")
        telemetry.update()
        currentState = SetViperBottomState.STARTED
    }

    override fun execute() {
        when (currentState) {
            SetViperBottomState.STARTED -> {
                telemetry.addLine("Started ${this.name}")
                telemetry.update()
                deliverySubsystem.setViperPowerAuton(1.0)
                currentState = SetViperBottomState.SEEKING
            }
            SetViperBottomState.SEEKING -> {
                telemetry.addLine("Seeking ${this.name}")
                telemetry.update()
                if (deliverySubsystem.isRetracted()) {
                    telemetry.addLine("Retracted ${this.name}")
                    telemetry.update()
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