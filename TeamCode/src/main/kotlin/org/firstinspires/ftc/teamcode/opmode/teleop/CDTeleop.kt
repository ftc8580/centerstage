package org.firstinspires.ftc.teamcode.opmode.teleop

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.bucket.CloseBucket
import org.firstinspires.ftc.teamcode.command.bucket.OpenBucket
import org.firstinspires.ftc.teamcode.command.delivery.DeliveryPositionOne
import org.firstinspires.ftc.teamcode.command.delivery.GoToBottomPosition
import org.firstinspires.ftc.teamcode.command.drone.LaunchDrone
import org.firstinspires.ftc.teamcode.command.suspend.DeployHooks
import org.firstinspires.ftc.teamcode.opmode.OpModeBase

@Suppress("UNUSED")
@TeleOp(name="CDTeleop")
class CDTeleop : OpModeBase() {
    private var driveSpeedScale = DRIVE_SPEED_NORMAL
    private var controlIntakeSeparately = true

    override fun initialize() {
        initHardware(false)
        initializeDriverGamepad(driverGamepad)
        initializeCoDriverGamepad(accessoryGamepad)
    }

    override fun run() {
        super.run()

        telemetry.clearAll()

        mecanumDrive.setDrivePower(
            Pose2d(
                driverGamepad.leftY * driveSpeedScale,
                -driverGamepad.leftX * driveSpeedScale,
                -driverGamepad.rightX * driveSpeedScale
            )
        )

        mecanumDrive.updatePoseEstimate()

        val leftTriggerValue = accessoryGamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)
        val rightTriggerValue = accessoryGamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)
        val intakeIsReversed = accessoryGamepad.isDown(GamepadKeys.Button.A)

        if (controlIntakeSeparately) {
            if (leftTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                intakeSubsystem?.setTransferPower(leftTriggerValue, intakeIsReversed)
            } else {
                intakeSubsystem?.setTransferPower(0.0)
            }

            if (rightTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                intakeSubsystem?.setIntakePower(rightTriggerValue, intakeIsReversed)
            } else {
                intakeSubsystem?.setIntakePower(0.0)
            }
        } else {
            if (rightTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                intakeSubsystem?.runIntake(rightTriggerValue, intakeIsReversed)
            } else {
                intakeSubsystem?.runIntake(0.0)
            }
        }

        if (accessoryGamepad.leftY > VARIABLE_INPUT_DEAD_ZONE || accessoryGamepad.leftY < -VARIABLE_INPUT_DEAD_ZONE) {
            deliverySubsystem?.setViperPower(-accessoryGamepad.leftY)
        } else {
            deliverySubsystem?.setViperPower(0.0)
        }

        if (accessoryGamepad.rightY > VARIABLE_INPUT_DEAD_ZONE || accessoryGamepad.rightY < -VARIABLE_INPUT_DEAD_ZONE) {
            suspendSubsystem?.setMotorPower(accessoryGamepad.rightY)
        } else {
            suspendSubsystem?.setMotorPower(0.0)
        }

        hardware.viperTouch?.let {
            if (it.isPressed) {
                deliverySubsystem?.setViperBottom()
            }
        }

        writeTelemetry()
    }

    private fun initializeDriverGamepad(gamepad: GamepadEx) {
        val speedFastButton = gamepad.getGamepadButton(GamepadKeys.Button.Y)
        val speedSlowButton = gamepad.getGamepadButton(GamepadKeys.Button.A)
        val normalDriveButton = gamepad.getGamepadButton(GamepadKeys.Button.B)
        val droneReleaseButtonA = gamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
        val droneReleaseButtonB = gamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
        val droneReleaseTrigger = droneReleaseButtonA.and(droneReleaseButtonB)

        speedFastButton.whenPressed(Runnable { driveSpeedScale = DRIVE_SPEED_FAST })
        speedSlowButton.whenPressed(Runnable { driveSpeedScale = DRIVE_SPEED_SLOW })
        normalDriveButton.whenPressed(Runnable { driveSpeedScale = DRIVE_SPEED_NORMAL})
        droneSubsystem?.let {
            droneReleaseTrigger.whenActive(LaunchDrone(it))
        }
    }

    private fun initializeCoDriverGamepad(gamepad: GamepadEx) {
        // Bucket
        val openBucketButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
        val closeBucketButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)

        // Viper arm
        // The arm angle buttons are reversed to fix an issue with the Servo direction
        val armAngleUpButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
        val armAngleDownButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_UP)
        val retractButton = gamepad.getGamepadButton(GamepadKeys.Button.X)
        val extendPositionOneButton = gamepad.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)

        // Intake controls
        val toggleIntakeControlButton = gamepad.getGamepadButton(GamepadKeys.Button.B)

        // Suspend
        val deployHooksButton = gamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)

        deliverySubsystem?.let {
            openBucketButton.whenPressed(OpenBucket(it))
            closeBucketButton.whenPressed(CloseBucket(it))
            retractButton.whenPressed(GoToBottomPosition(it))
            extendPositionOneButton.whenPressed(DeliveryPositionOne(it))
        }

        armAngleUpButton.whenActive(Runnable {
            val currentServoPosition = hardware.viperAngleServo?.position ?: 0.0
            hardware.viperAngleServo?.position = currentServoPosition + VIPER_ANGLE_SERVO_INCREMENT
        })

        armAngleDownButton.whenActive(Runnable {
            val currentServoPosition = hardware.viperAngleServo?.position ?: 0.0
            hardware.viperAngleServo?.position = currentServoPosition - VIPER_ANGLE_SERVO_INCREMENT
        })

        intakeSubsystem?.let {
            toggleIntakeControlButton.whenPressed(Runnable { controlIntakeSeparately = !controlIntakeSeparately })
        }

        suspendSubsystem?.let {
            deployHooksButton.whenPressed(DeployHooks(it))
        }
    }

    private fun writeTelemetry() {
        telemetry.addLine()
        telemetry.addLine("speed mult: $driveSpeedScale")
        telemetry.addLine()

        hardware.suspendMotor?.let {
            telemetry.addLine("suspend motor pos: ${it.currentPosition}")
        } ?: telemetry.addLine("[WARNING] Suspend motor not found")

        hardware.viperAngleServo?.let {
            telemetry.addLine("viper angle pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] Viper angle servo not found")

        hardware.viperPot?.let {
            telemetry.addLine("pot voltage: ${it.voltage}")
        } ?: telemetry.addLine("[WARNING] Viper potentiometer not found")

        hardware.viperMotor?.let {
            telemetry.addLine("viper motor pos: ${it.currentPosition}")
        } ?: telemetry.addLine("[WARNING] Viper motor not found")

        hardware.droneServo?.let {
            telemetry.addLine("drone pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] Drone servo not found")

        hardware.suspendServoLeft?.let {
            telemetry.addLine("suspendServoLeft pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] suspendServoLeft not found")

        hardware.suspendServoRight?.let {
            telemetry.addLine("suspendServoRight pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] suspendServoRight servo not found")

        telemetry.update()
    }

    companion object {
        private const val VARIABLE_INPUT_DEAD_ZONE = 0.05

        private const val DRIVE_SPEED_FAST = 0.9
        private const val DRIVE_SPEED_NORMAL = 0.75
        private const val DRIVE_SPEED_SLOW = 0.5

        // TODO: Testing only, remove after tuned
        private const val VIPER_ANGLE_SERVO_INCREMENT = 0.02
    }
}