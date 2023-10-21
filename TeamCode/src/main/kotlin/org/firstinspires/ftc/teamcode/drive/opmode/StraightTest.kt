package org.firstinspires.ftc.teamcode.drive.opmode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.drive.CDMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

/*
 * This is a simple routine to test translational drive capabilities.
 */
@Config
@Autonomous(group = "drive")
@Disabled
class StraightTest : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val telemetry: Telemetry =
            MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val drive = CDMecanumDrive(HardwareManager(CDConfig(), hardwareMap))
        val trajectory = drive.trajectoryBuilder(Pose2d())
            .forward(DISTANCE)
            .build()
        waitForStart()
        if (isStopRequested) return
        drive.followTrajectory(trajectory)
        val (x, y, heading) = drive.poseEstimate
        telemetry.addData("finalX", x)
        telemetry.addData("finalY", y)
        telemetry.addData("finalHeading", heading)
        telemetry.update()
        while (!isStopRequested && opModeIsActive());
    }

    companion object {
        var DISTANCE = 60.0 // in
    }
}