package org.firstinspires.ftc.teamcode.command.delivery

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.util.CDRuntime

class DeliverPixel(private val deliverySubsystem: DeliverySubsystem, private val telemetry: MultipleTelemetry? = null) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = DeliverPixelState.IDLE
    private var targetTimeMs = 0.0
    private val runtime = CDRuntime()

    override fun initialize() {
        targetTimeMs = deliverySubsystem.getOpenBucketMs() * 3
        currentState = DeliverPixelState.STARTED
    }

    override fun execute() {
        // This is a state machine
        when (currentState) {
            DeliverPixelState.STARTED -> {
                telemetry?.addLine("DeliverPixel Started")
                telemetry?.update()
                deliverySubsystem.setViperExtension(25.0)
                currentState = DeliverPixelState.RAISING
            }
            DeliverPixelState.RAISING -> {
                telemetry?.addLine("DeliverPixel Raising")
                telemetry?.update()
                if (deliverySubsystem.isStopped()) {
                    runtime.reset()
                    deliverySubsystem.openBucket()
                    currentState = DeliverPixelState.DROPPING_PIXEL
                }
            }
            DeliverPixelState.DROPPING_PIXEL -> {
                telemetry?.addLine("DeliverPixel Dropping Pixel")
                telemetry?.update()
                if (runtime.isTimedOut(targetTimeMs)) {
                    deliverySubsystem.closeBucket()
                    deliverySubsystem.setViperExtension(0.0)
                    currentState = DeliverPixelState.LOWERING
                }
            }
            DeliverPixelState.LOWERING -> {
                telemetry?.addLine("DeliverPixel Lowering")
                telemetry?.update()
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
            deliverySubsystem.setViperExtension(0.0)
        }

        telemetry?.addLine("DeliverPixel ending")
        telemetry?.update()

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