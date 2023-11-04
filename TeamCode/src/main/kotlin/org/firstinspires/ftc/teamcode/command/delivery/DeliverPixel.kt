package org.firstinspires.ftc.teamcode.command.delivery

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.util.CDRuntime

class DeliverPixel(private val deliverySubsystem: DeliverySubsystem) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = DeliverPixelState.IDLE
    private var targetTimeMs = 0.0
    private val runtime = CDRuntime()

    override fun initialize() {
        targetTimeMs = deliverySubsystem.getOpenBucketMs()
        currentState = DeliverPixelState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            DeliverPixelState.STARTED -> {
                runtime.reset()
                deliverySubsystem.setViperPosition(DELIVERY_HEIGHT)
                currentState = DeliverPixelState.RAISING
            }
            DeliverPixelState.RAISING -> {
                if (deliverySubsystem.isStopped()) {
                    deliverySubsystem.openBucket()
                    currentState = DeliverPixelState.DROPPING_PIXEL
                }
            }
            DeliverPixelState.DROPPING_PIXEL -> {
                if (runtime.isTimedOut(targetTimeMs)) {
                    deliverySubsystem.closeBucket()
                    deliverySubsystem.setViperPosition(INTAKE_HEIGHT)
                    currentState = DeliverPixelState.LOWERING
                }
            }
            DeliverPixelState.LOWERING -> {
                if (deliverySubsystem.isStopped()) {
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
            deliverySubsystem.setViperPosition(INTAKE_HEIGHT)
        }

        currentState = DeliverPixelState.IDLE
    }

    companion object {
        // TODO: Use real values
        private const val DELIVERY_HEIGHT = 10
        private const val INTAKE_HEIGHT = 0

        enum class DeliverPixelState {
            IDLE,
            STARTED,
            RAISING,
            DROPPING_PIXEL,
            LOWERING,
            FINISHED
        }
    }
}