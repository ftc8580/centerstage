package org.firstinspires.ftc.teamcode.opmode.teleop

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.bucket.CloseBucket
import org.firstinspires.ftc.teamcode.command.bucket.OpenBucket
import org.firstinspires.ftc.teamcode.command.drone.LaunchDrone
import org.firstinspires.ftc.teamcode.opmode.OpModeBase
import org.firstinspires.ftc.teamcode.util.PIDController

@Suppress("UNUSED")
@TeleOp(name="CDTeleop")
class CDTeleop : OpModeBase() {
    private var driveSpeedScale = DRIVE_SPEED_NORMAL
    private var viperPid = PIDController()

    override fun initialize() {
        initHardware(false)
        initializeDriverGamepad(driverGamepad)
        initializeCoDriverGamepad(accessoryGamepad)
    }

    override fun run() {
        super.run()

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

        if (accessoryGamepad.isDown(GamepadKeys.Button.A)) {
            if (leftTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                hardware.transferMotor?.power = -leftTriggerValue
            } else {
                hardware.transferMotor?.power = 0.0
            }

            if (rightTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                hardware.intakeMotor?.power = -rightTriggerValue
            } else {
                hardware.intakeMotor?.power = 0.0
            }
        } else {
            if (leftTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                hardware.transferMotor?.power = leftTriggerValue
            } else {
                hardware.transferMotor?.power = 0.0
            }

            if (rightTriggerValue > VARIABLE_INPUT_DEAD_ZONE) {
                hardware.intakeMotor?.power = rightTriggerValue
            } else {
                hardware.intakeMotor?.power = 0.0
            }
        }

        if (accessoryGamepad.leftY > VARIABLE_INPUT_DEAD_ZONE || accessoryGamepad.leftY < -VARIABLE_INPUT_DEAD_ZONE) {
            deliverySubsystem?.setViperPower(-accessoryGamepad.leftY, viperPid)
        } else {
            deliverySubsystem?.setViperPower(0.0)
        }

        writeTelemetry()
    }

    private fun initializeDriverGamepad(gamepad: GamepadEx) {
        val speedFastButton = gamepad.getGamepadButton(GamepadKeys.Button.Y)
        val speedSlowButton = gamepad.getGamepadButton(GamepadKeys.Button.A)
        val normalDriveButton = gamepad.getGamepadButton(GamepadKeys.Button.B)
        val droneReleaseButton = gamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)

        speedFastButton.whenPressed(Runnable { driveSpeedScale = DRIVE_SPEED_FAST })
        speedSlowButton.whenPressed(Runnable { driveSpeedScale = DRIVE_SPEED_SLOW })
        normalDriveButton.whenPressed(Runnable { driveSpeedScale = DRIVE_SPEED_NORMAL})
        if (droneSubsystem != null) {
            droneReleaseButton.whenPressed(LaunchDrone(droneSubsystem!!))
        }
    }

    private fun initializeCoDriverGamepad(gamepad: GamepadEx) {
        val openBucketButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
        val closeBucketButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
        val armAngleUpButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_UP)
        val armAngleDownButton = gamepad.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
        val clearIntakeButton = gamepad.getGamepadButton(GamepadKeys.Button.Y)
        val shortModeButton = gamepad.getGamepadButton(GamepadKeys.Button.X)
        // TODO: get the rest of the buttons

        // TODO: Assign commands to buttons
        if (deliverySubsystem != null) {
            openBucketButton.whenPressed(OpenBucket(deliverySubsystem!!))
            closeBucketButton.whenPressed(CloseBucket(deliverySubsystem!!))
        }

        armAngleUpButton.whenActive(Runnable {
            val currentServoPosition = hardware.viperAngleServo?.position ?: 0.0
            hardware.viperAngleServo?.position = currentServoPosition + VIPER_ANGLE_SERVO_INCREMENT
        })

        armAngleDownButton.whenActive(Runnable {
            val currentServoPosition = hardware.viperAngleServo?.position ?: 0.0
            hardware.viperAngleServo?.position = currentServoPosition - VIPER_ANGLE_SERVO_INCREMENT
        })
    }

    private fun writeTelemetry() {
        telemetry.clearAll()

        telemetry.addLine("speed mult: $driveSpeedScale")
        telemetry.addLine()

        hardware.viperAngleServo?.let {
            telemetry.addLine("viper angle pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] Viper angle servo not found")

        hardware.viperPot?.let {
            telemetry.addLine("pot voltage: ${it.voltage}")
        } ?: telemetry.addLine("[WARNING] Viper potentiometer not found")

        hardware.viperMotor?.let {
            telemetry.addLine("viper motor pos: ${it.currentPosition}")
        } ?: telemetry.addLine("[WARNING] Viper motor not found")

        hardware.bucketServo?.let {
            telemetry.addLine("bucket pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] Bucket servo not found")

        hardware.droneServo?.let {
            telemetry.addLine("drone pos: ${it.position}")
        } ?: telemetry.addLine("[WARNING] Drone servo not found")

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