package org.firstinspires.ftc.teamcode.command.delivery

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystem.DeliverySubsystem
import org.firstinspires.ftc.teamcode.util.isTimedOut

class DeliverPixel(private val deliverySubsystem: DeliverySubsystem, private val telemetry: MultipleTelemetry? = null) : CommandBase() {
    init {
        addRequirements(deliverySubsystem)
    }

    private var currentState = DeliverPixelState.IDLE
    private var targetTimeMs = 0.0
    private val runtime = ElapsedTime()

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
                    runtime.reset()
                    deliverySubsystem.setAngleHigh()
                    currentState = DeliverPixelState.ANGLE_UP
                }
            }
            DeliverPixelState.ANGLE_UP -> {
                if (runtime.isTimedOut(targetTimeMs)) {
                    runtime.reset()
                    deliverySubsystem.setAngleLow()
                    currentState = DeliverPixelState.ANGLE_DOWN
                }
            }
            DeliverPixelState.ANGLE_DOWN -> {
                if (runtime.isTimedOut(targetTimeMs)) {
                    runtime.reset()
                    deliverySubsystem.setViperExtension(0.0)
                    currentState = DeliverPixelState.LOWERING
                }
            }
            DeliverPixelState.LOWERING -> {
                telemetry?.addLine("DeliverPixel Lowering")
                telemetry?.update()
                if (deliverySubsystem.isStopped() || runtime.isTimedOut(1500.0)) {
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

        telemetry?.addLine("DeliverPixel ending")
        telemetry?.update()

        currentState = DeliverPixelState.IDLE
    }

    companion object {
        enum class DeliverPixelState {
            IDLE,
            STARTED,
            RAISING,
            DROPPING_PIXEL,
            ANGLE_UP,
            ANGLE_DOWN,
            LOWERING,
            FINISHED
        }
    }
}