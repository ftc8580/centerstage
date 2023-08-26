package org.firstinspires.ftc.teamcode.drive.opmode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

/*
 * This is a simple routine to test turning capabilities.
 */
@Config
@Autonomous(group = "drive")
class TurnTest : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val drive = SampleMecanumDrive(HardwareManager(CDConfig(), hardwareMap))
        waitForStart()
        if (isStopRequested) return
        drive.turn(Math.toRadians(ANGLE))
    }

    companion object {
        var ANGLE = 90.0 // deg
    }
}