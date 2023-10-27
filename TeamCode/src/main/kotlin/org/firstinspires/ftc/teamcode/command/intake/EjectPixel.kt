package org.firstinspires.ftc.teamcode.command.intake

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.command.intake.model.IntakeState
import org.firstinspires.ftc.teamcode.subsystem.TransferSubsystem

class EjectPixel(private val transferSubsystem: TransferSubsystem) : CommandBase() {
    init {
        addRequirements(transferSubsystem)
    }

    private var currentState = IntakeState.IDLE

    override fun initialize() {
        currentState = IntakeState.STARTED
    }

    override fun execute() {
        when (currentState) {
            IntakeState.STARTED -> {
                transferSubsystem.runEject()
                currentState = IntakeState.EJECTING
            }
            IntakeState.EJECTING -> {
                // TODO: Watch for signal of pixel being picked up and then stop
                if (true) {
                    transferSubsystem.stop()
                    currentState = IntakeState.FINISHED
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == IntakeState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        if (interrupted) {
            transferSubsystem.stop()
        }

        currentState = IntakeState.IDLE
    }
}