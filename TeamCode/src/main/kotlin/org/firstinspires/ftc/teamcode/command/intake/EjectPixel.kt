package org.firstinspires.ftc.teamcode.command.intake

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.command.intake.model.IntakeState
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem

class EjectPixel(private val intakeSubsystem: IntakeSubsystem) : CommandBase() {
    init {
        addRequirements(intakeSubsystem)
    }

    private var currentState = IntakeState.IDLE

    override fun initialize() {
        currentState = IntakeState.STARTED
    }

    override fun execute() {
        when (currentState) {
            IntakeState.STARTED -> {
                intakeSubsystem.runEject()
                currentState = IntakeState.EJECTING
            }
            IntakeState.EJECTING -> {
                // TODO: Watch for signal of pixel being picked up and then stop
                if (true) {
                    intakeSubsystem.stop()
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
            intakeSubsystem.stop()
        }

        currentState = IntakeState.IDLE
    }
}