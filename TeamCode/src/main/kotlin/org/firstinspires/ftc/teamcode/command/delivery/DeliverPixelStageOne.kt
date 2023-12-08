package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.command.delivery.model.DeliverPixelState
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.util.isTimedOut

class DeliverPixelStageOne(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = DeliverPixelState.IDLE
    private var targetTimeMs = 0.0
    private val runtime = ElapsedTime()

    override fun initialize() {
        targetTimeMs = deliverySubsystem.getOpenBucketMs()
        currentState = DeliverPixelState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            DeliverPixelState.STARTED -> {
                deliverySubsystem.setViperExtension(25.0)
                currentState = DeliverPixelState.RAISING
            }
            DeliverPixelState.RAISING -> {
                if (deliverySubsystem.isStopped()) {
                    runtime.reset()
                    deliverySubsystem.openBucket()
                    currentState = DeliverPixelState.DROPPING_PIXEL
                }
            }
            DeliverPixelState.DROPPING_PIXEL -> {
                if (runtime.isTimedOut(targetTimeMs)) {
                    runtime.reset()
                    deliverySubsystem.setAngleHigh()
                    currentState = DeliverPixelState.ANGLE_UP
                }
            }
            DeliverPixelState.ANGLE_UP -> {
                if (runtime.isTimedOut(targetTimeMs)) {
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