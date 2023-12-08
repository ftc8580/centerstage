package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.command.delivery.model.DeliverPixelState
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.util.isTimedOut

class DeliverPixelStageTwo(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = DeliverPixelState.IDLE
    private val runtime = ElapsedTime()
    private val angleChangeTime = 500.0

    override fun initialize() {
        currentState = DeliverPixelState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            DeliverPixelState.STARTED -> {
                runtime.reset()
                deliverySubsystem.setAngleLow()
                currentState = DeliverPixelState.ANGLE_DOWN
            }
            DeliverPixelState.ANGLE_DOWN -> {
                if (runtime.isTimedOut(angleChangeTime)) {
                    deliverySubsystem.setViperPowerAuton(1.0)
                    runtime.reset()
                    currentState = DeliverPixelState.LOWERING
                }
            }
            DeliverPixelState.LOWERING -> {
                if (deliverySubsystem.isRetracted() || runtime.isTimedOut(1000.0)) {
                    deliverySubsystem.setViperBottom()
                    deliverySubsystem.closeBucket()
                    currentState = DeliverPixelState.FINISHED
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun isFinished(): Boolean {
        return currentState == DeliverPixelState.FINISHED
    }

    override fun end(interrupted: Boolean) {
        if (interrupted) {
            deliverySubsystem.setViperExtension(0.0)
        }

        currentState = DeliverPixelState.IDLE
    }
}