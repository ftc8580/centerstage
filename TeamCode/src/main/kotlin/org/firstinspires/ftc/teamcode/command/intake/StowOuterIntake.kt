package org.firstinspires.ftc.teamcode.command.intake

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.command.intake.model.IntakeHeightState
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem
import org.firstinspires.ftc.teamcode.util.CDRuntime

class StowOuterIntake(private val intakeSubsystem: IntakeSubsystem) : CommandBase() {
    init {
        addRequirements(intakeSubsystem)
    }

    private var currentState = IntakeHeightState.IDLE
    private var targetTimeMs = 0.0
    private val runtime = CDRuntime()

    override fun initialize() {
        targetTimeMs = intakeSubsystem.getMoveTimeMs(SERVO_HIGH_POSITION)
        currentState = IntakeHeightState.STARTED
    }

    override fun execute() {
        when (currentState) {
            IntakeHeightState.STARTED -> {
                runtime.reset()
                intakeSubsystem.moveTo(SERVO_HIGH_POSITION)
                currentState = IntakeHeightState.RAISING
            }
            IntakeHeightState.RAISING -> {
                if (runtime.isTimedOut(targetTimeMs)) {
                    currentState = IntakeHeightState.UP
                }
            }
            IntakeHeightState.UP -> {
                currentState = IntakeHeightState.FINISHED
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == IntakeHeightState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        if (interrupted) {
            intakeSubsystem.moveTo(SERVO_HIGH_POSITION)
        }

        currentState = IntakeHeightState.IDLE
    }

    companion object {
        // TODO: Use real value
        private const val SERVO_HIGH_POSITION = 0.0
    }
}