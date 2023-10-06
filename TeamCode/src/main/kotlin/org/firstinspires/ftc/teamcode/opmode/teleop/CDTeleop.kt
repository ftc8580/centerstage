package org.firstinspires.ftc.teamcode.opmode.teleop

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.opmode.OpModeBase


@TeleOp(name="CDTeleop")
class CDTeleop : OpModeBase() {
    private var touchCount = 0
    private var lastDistance = 0.0

    override fun initialize() {
        initHardware(false)
    }

    override fun run() {
        super.run()

        mecanumDrive.setDrivePower(
            Pose2d(
                gamepad1.left_stick_y.toDouble() * driveSpeedScale,
                gamepad1.left_stick_x.toDouble() * driveSpeedScale,
                gamepad1.right_stick_x.toDouble() * driveSpeedScale
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

    companion object {
        const val driveSpeedScale = 0.5
    }
}