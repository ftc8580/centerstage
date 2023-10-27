package org.firstinspires.ftc.teamcode.command.intake

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.command.intake.model.IntakeHeightState
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem
import org.firstinspires.ftc.teamcode.util.CDRuntime

class LowerOuterIntake(private val intakeSubsystem: IntakeSubsystem) : CommandBase() {
    init {
        addRequirements(intakeSubsystem)
    }

    private var currentState = IntakeHeightState.IDLE
    private var targetTimeMs = 0.0
    private val runtime = CDRuntime()

    override fun initialize() {
        targetTimeMs = intakeSubsystem.getMoveTimeMs(SERVO_LOW_POSITION)
        currentState = IntakeHeightState.STARTED
    }

    override fun execute() {
        when (currentState) {
            IntakeHeightState.STARTED -> {
                runtime.reset()
                intakeSubsystem.moveTo(SERVO_LOW_POSITION)
                currentState = IntakeHeightState.LOWERING
            }
            IntakeHeightState.LOWERING -> {
                if (runtime.isTimedOut(targetTimeMs)) {
                    currentState = IntakeHeightState.DOWN
                }
            }
            IntakeHeightState.DOWN -> {
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
        // TODO: Use real values
        private const val SERVO_LOW_POSITION = 0.9
        private const val SERVO_HIGH_POSITION = 0.0
    }
}