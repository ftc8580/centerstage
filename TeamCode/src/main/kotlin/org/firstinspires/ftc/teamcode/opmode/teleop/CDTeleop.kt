package org.firstinspires.ftc.teamcode.opmode.teleop

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.command.bucket.CloseBucket
import org.firstinspires.ftc.teamcode.command.bucket.OpenBucket
import org.firstinspires.ftc.teamcode.command.drone.LaunchDrone
import org.firstinspires.ftc.teamcode.opmode.OpModeBase


@TeleOp(name="CDTeleop")
class CDTeleop : OpModeBase() {
//    private var touchCount = 0
//    private var lastDistance = 0.0

    private var driveSpeedScale = DRIVE_SPEED_NORMAL

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
                driverGamepad.leftX * driveSpeedScale,
                driverGamepad.rightX * driveSpeedScale
            )
        )

        mecanumDrive.updatePoseEstimate()

        val leftTriggerValue = accessoryGamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)
        val rightTriggerValue = accessoryGamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)

        if (accessoryGamepad.isDown(GamepadKeys.Button.A)) {
            if (leftTriggerValue > 0.01) {
                hardware.transferMotor?.power = -leftTriggerValue
            }
            if (rightTriggerValue > 0.01) {
                hardware.intakeMotor?.power = -rightTriggerValue
            }
        } else {
            if (leftTriggerValue > 0.01) {
                hardware.transferMotor?.power = leftTriggerValue
            }
            if (rightTriggerValue > 0.01) {
                hardware.intakeMotor?.power = rightTriggerValue
            }
        }

        if (accessoryGamepad.leftY > 0.01 || accessoryGamepad.leftY < -0.01) {
            hardware.viperMotor?.power = accessoryGamepad.leftY
        }

        telemetry.clearAll()

        deliverySubsystem?.let {
            telemetry.addLine("delivery sys: true")
        } ?: telemetry.addLine("delivery sys: false")

        hardware.bucketServo?.let {
            telemetry.addLine("bucket pos: ${it.position}")
        }

        telemetry.addLine("speed mult: $driveSpeedScale")

        telemetry.update()

        // Test sensors
//        telemetry.clearAll()
//
//        if (hardware.touchSensor?.isPressed == true) {
//            touchCount += 1
//        }
//
//        val currentDistance = hardware.distanceSensor?.getDistance(DistanceUnit.INCH)
//
//        if (currentDistance != null && currentDistance != lastDistance) {
//            lastDistance = currentDistance
//        }
//
//        telemetry.addLine("Pressed touch sensor $touchCount")
//        telemetry.addLine("Distance changed: $lastDistance in")
//
//        if (hardware.colorSensor != null) {
//            val red = hardware.colorSensor!!.red()
//            val green = hardware.colorSensor!!.green()
//            val blue = hardware.colorSensor!!.blue()
//            telemetry.addLine("Color values: $red, $green, $blue")
//        }
//
//        telemetry.update()
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
    }

    companion object {
        private const val DRIVE_SPEED_FAST = 0.9
        private const val DRIVE_SPEED_NORMAL = 0.75
        private const val DRIVE_SPEED_SLOW = 0.5
    }
}