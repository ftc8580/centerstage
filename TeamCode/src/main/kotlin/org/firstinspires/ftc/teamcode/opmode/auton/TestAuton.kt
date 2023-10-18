package org.firstinspires.ftc.teamcode.opmode.auton

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.opmode.OpModeBase

@Autonomous(group = "test")
class TestAuton : OpModeBase() {
    override fun initialize() {
        initHardware(true)

        val trajectorySequence = mecanumDrive.trajectorySequenceBuilder(Pose2d())
            .forward(20.0)
            .strafeRight(20.0)
            .forward(15.0)
            .strafeLeft(40.0)
            .splineTo(Vector2d(-20.0, -20.0), Math.toRadians(90.0))
            .build()

        mecanumDrive.followTrajectorySequence(trajectorySequence)
    }
}