package org.firstinspires.ftc.teamcode.drive.opmode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.kinematics.Kinematics.calculateMotorFeedforward
import com.acmerobotics.roadrunner.profile.MotionProfile
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator.generateSimpleMotionProfile
import com.acmerobotics.roadrunner.profile.MotionState
import com.acmerobotics.roadrunner.util.NanoClock
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.config.DriveConstants
import org.firstinspires.ftc.teamcode.drive.CDMecanumDrive
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

/*
 * This routine is designed to tune the open-loop feedforward coefficients. Although it may seem unnecessary,
 * tuning these coefficients is just as important as the positional parameters. Like the other
 * manual tuning routines, this op mode relies heavily upon the dashboard. To access the dashboard,
 * connect your computer to the RC's WiFi network. In your browser, navigate to
 * https://192.168.49.1:8080/dash if you're using the RC phone or https://192.168.43.1:8080/dash if
 * you are using the Control Hub. Once you've successfully connected, start the program, and your
 * robot will begin moving forward and backward according to a motion profile. Your job is to graph
 * the velocity errors over time and adjust the feedforward coefficients. Once you've found a
 * satisfactory set of gains, add them to the appropriate fields in the DriveConstants.java file.
 *
 * Pressing Y/Δ (Xbox/PS4) will pause the tuning process and enter driver override, allowing the
 * user to reset the position of the bot in the event that it drifts off the path.
 * Pressing B/O (Xbox/PS4) will cede control back to the tuning process.
 */
@Config
@Autonomous(group = "drive")
@Disabled
class ManualFeedforwardTuner : LinearOpMode() {
    private val dashboard = FtcDashboard.getInstance()
    private var drive: CDMecanumDrive? = null

    internal enum class Mode {
        DRIVER_MODE, TUNING_MODE
    }

    private var mode: Mode = Mode.TUNING_MODE

    override fun runOpMode() {
        if (DriveConstants.RUN_USING_ENCODER) {
            RobotLog.setGlobalErrorMsg(
                "Feedforward constants usually don't need to be tuned " +
                        "when using the built-in drive motor velocity PID."
            )
        }
        val telemetry: Telemetry = MultipleTelemetry(telemetry, dashboard.telemetry)
        drive = CDMecanumDrive(HardwareManager(CDConfig(), hardwareMap))
        val voltageSensor = hardwareMap.voltageSensor.iterator().next()
        mode = Mode.TUNING_MODE
        val clock = NanoClock.system()
        telemetry.addLine("Ready!")
        telemetry.update()
        telemetry.clearAll()
        waitForStart()
        if (isStopRequested) return
        var movingForwards = true
        var activeProfile = generateProfile(true)
        var profileStart = clock.seconds()
        while (!isStopRequested) {
            telemetry.addData("mode", mode)
            when (mode) {
                Mode.TUNING_MODE -> {
                    if (gamepad1.y) {
                        mode = Mode.DRIVER_MODE
                    }

                    // calculate and set the motor power
                    val profileTime = clock.seconds() - profileStart
                    if (profileTime > activeProfile.duration()) {
                        // generate a new profile
                        movingForwards = !movingForwards
                        activeProfile = generateProfile(movingForwards)
                        profileStart = clock.seconds()
                    }
                    val motionState = activeProfile[profileTime]
                    val targetPower = calculateMotorFeedforward(
                        motionState.v,
                        motionState.a,
                        DriveConstants.kV,
                        DriveConstants.kA,
                        DriveConstants.kStatic
                    )
                    val nominalVoltage = 12.0
                    val voltage = voltageSensor.voltage
                    drive!!.setDrivePower(Pose2d(nominalVoltage / voltage * targetPower, 0.0, 0.0))
                    drive!!.updatePoseEstimate()
                    val currentVelo = drive!!.poseVelocity

                    // update telemetry
                    telemetry.addData("targetVelocity", motionState.v)
                    telemetry.addData("measuredVelocity", currentVelo?.x ?: 0.0)
                    telemetry.addData("error", motionState.v - (currentVelo?.x ?: 0.0))
                }

                Mode.DRIVER_MODE -> {
                    if (gamepad1.b) {
                        mode = Mode.TUNING_MODE
                        movingForwards = true
                        activeProfile = generateProfile(movingForwards)
                        profileStart = clock.seconds()
                    }
                    drive!!.setWeightedDrivePower(
                        Pose2d(
                            -gamepad1.left_stick_y.toDouble(),
                            -gamepad1.left_stick_x.toDouble(),
                            -gamepad1.right_stick_x.toDouble()
                        )
                    )
                }
            }
            telemetry.update()
        }
    }

    companion object {
        @JvmField var DISTANCE = 72.0 // in
        private fun generateProfile(movingForward: Boolean): MotionProfile {
            val start = MotionState(if (movingForward) 0.0 else DISTANCE, 0.0, 0.0, 0.0)
            val goal = MotionState(if (movingForward) DISTANCE else 0.0, 0.0, 0.0, 0.0)
            return generateSimpleMotionProfile(
                start,
                goal,
                DriveConstants.MAX_VEL,
                DriveConstants.MAX_ACCEL
            )
        }
    }
}