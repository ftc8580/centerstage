package org.firstinspires.ftc.teamcode.drive.opmode

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive

/*
 * Op mode for preliminary tuning of the follower PID coefficients (located in the drive base
 * classes). The robot drives in a DISTANCE-by-DISTANCE square indefinitely. Utilization of the
 * dashboard is recommended for this tuning routine. To access the dashboard, connect your computer
 * to the RC's WiFi network. In your browser, navigate to https://192.168.49.1:8080/dash if you're
 * using the RC phone or https://192.168.43.1:8080/dash if you are using the Control Hub. Once
 * you've successfully connected, start the program, and your robot will begin driving in a square.
 * You should observe the target position (green) and your pose estimate (blue) and adjust your
 * follower PID coefficients such that you follow the target position as accurately as possible.
 * If you are using SampleMecanumDrive, you should be tuning TRANSLATIONAL_PID and HEADING_PID.
 * These coefficients can be tuned live in dashboard.
 */
@Config
@Autonomous(group = "drive")
class FollowerPIDTuner : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val drive = SampleMecanumDrive(hardwareMap)
        val startPose = Pose2d(-DISTANCE / 2, -DISTANCE / 2, 0.0)
        drive.poseEstimate = startPose
        waitForStart()
        if (isStopRequested) return
        while (!isStopRequested) {
            val trajSeq = drive.trajectorySequenceBuilder(startPose)
                .forward(DISTANCE)
                .turn(Math.toRadians(90.0))
                .forward(DISTANCE)
                .turn(Math.toRadians(90.0))
                .forward(DISTANCE)
                .turn(Math.toRadians(90.0))
                .forward(DISTANCE)
                .turn(Math.toRadians(90.0))
                .build()
            drive.followTrajectorySequence(trajSeq)
        }
    }

    companion object {
        var DISTANCE = 48.0 // in
    }
}