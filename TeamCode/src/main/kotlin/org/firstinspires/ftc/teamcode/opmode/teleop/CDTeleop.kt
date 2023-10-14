package org.firstinspires.ftc.teamcode.opmode.teleop

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.opmode.OpModeBase


@TeleOp(name="CDTeleop")
class CDTeleop : OpModeBase() {
    private var touchCount = 0
    private var lastDistance = 0.0

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

        // Test sensors
        telemetry.clearAll()

        if (hardware.touchSensor?.isPressed == true) {
            touchCount += 1
        }

        val currentDistance = hardware.distanceSensor?.getDistance(DistanceUnit.INCH)

        if (currentDistance != null && currentDistance != lastDistance) {
            lastDistance = currentDistance
        }

        telemetry.addLine("Pressed touch sensor $touchCount")
        telemetry.addLine("Distance changed: $lastDistance in")

        if (hardware.colorSensor != null) {
            val red = hardware.colorSensor!!.red()
            val green = hardware.colorSensor!!.green()
            val blue = hardware.colorSensor!!.blue()
            telemetry.addLine("Color values: $red, $green, $blue")
        }

        telemetry.update()
    }

    private fun initializeDriverGamepad(gamepad: GamepadEx) {
        val speedFastButton = gamepad.getGamepadButton(GamepadKeys.Button.Y)
        val speedSlowButton = gamepad.getGamepadButton(GamepadKeys.Button.A)
        val normalDriveButton = gamepad.getGamepadButton(GamepadKeys.Button.B)
        val droneReleaseButton = gamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)

        // TODO: Assign commands to buttons
        // Ex: speedFastButton.whenPressed(command)
    }

    private fun initializeCoDriverGamepad(gamepad: GamepadEx) {
        val intakeDownButton = gamepad.getGamepadButton(GamepadKeys.Button.A)
        val clearIntakeButton = gamepad.getGamepadButton(GamepadKeys.Button.Y)
        val shortModeButton = gamepad.getGamepadButton(GamepadKeys.Button.X)
        // TODO: get the rest of the buttons

        // TODO: Assign commands to buttons
        // Ex: intakeDownButton.whenPressed(command)
    }

    companion object {
        const val driveSpeedScale = 0.5
    }
}