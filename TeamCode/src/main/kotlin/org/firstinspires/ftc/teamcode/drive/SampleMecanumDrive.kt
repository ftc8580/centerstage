package org.firstinspires.ftc.teamcode.drive

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.drive.MecanumDrive
import com.acmerobotics.roadrunner.followers.HolonomicPIDVAFollower
import com.acmerobotics.roadrunner.followers.TrajectoryFollower
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.localization.Localizer
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint
import org.firstinspires.ftc.teamcode.config.DriveConstants
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceRunner
import kotlin.math.abs

/*
 * Simple mecanum drive hardware implementation for REV hardware.
 */
@Config
class SampleMecanumDrive(private val hardware: HardwareManager) : MecanumDrive(
    DriveConstants.kV,
    DriveConstants.kA,
    DriveConstants.kStatic,
    DriveConstants.TRACK_WIDTH,
    DriveConstants.TRACK_WIDTH,
    LATERAL_MULTIPLIER
) {
    private val trajectorySequenceRunner: TrajectorySequenceRunner
    private val follower: TrajectoryFollower
    private val lastEncPositions: MutableList<Int> = ArrayList()
    private val lastEncVels: MutableList<Int> = ArrayList()

    override var localizer = StandardTrackingWheelLocalizer(
        hardwareMap = hardware.hardwareMap,
        lastEncPositions = lastEncPositions,
        lastEncVels = lastEncVels
    ) as Localizer

    init {
        follower = HolonomicPIDVAFollower(
            TRANSLATIONAL_PID, TRANSLATIONAL_PID, HEADING_PID,
            Pose2d(0.5, 0.5, Math.toRadians(5.0)), 0.5
        )

        val lastTrackingEncPositions: List<Int> = ArrayList()
        val lastTrackingEncVels: List<Int> = ArrayList()

        // TODO: if desired, use setLocalizer() to change the localization method
        // setLocalizer(new StandardTrackingWheelLocalizer(hardwareMap, lastTrackingEncPositions, lastTrackingEncVels));
        trajectorySequenceRunner = TrajectorySequenceRunner(
            follower, HEADING_PID, hardware.batteryVoltageSensor,
            lastEncPositions, lastEncVels, lastTrackingEncPositions, lastTrackingEncVels
        )
    }

    fun trajectoryBuilder(startPose: Pose2d?): TrajectoryBuilder {
        return TrajectoryBuilder(startPose!!, false, VEL_CONSTRAINT, ACCEL_CONSTRAINT)
    }

    fun trajectoryBuilder(startPose: Pose2d?, reversed: Boolean): TrajectoryBuilder {
        return TrajectoryBuilder(startPose!!, reversed, VEL_CONSTRAINT, ACCEL_CONSTRAINT)
    }

    fun trajectoryBuilder(startPose: Pose2d?, startHeading: Double): TrajectoryBuilder {
        return TrajectoryBuilder(startPose!!, startHeading, VEL_CONSTRAINT, ACCEL_CONSTRAINT)
    }

    fun trajectorySequenceBuilder(startPose: Pose2d): TrajectorySequenceBuilder {
        return TrajectorySequenceBuilder(
            startPose,
            VEL_CONSTRAINT, ACCEL_CONSTRAINT,
            DriveConstants.MAX_ANG_VEL, DriveConstants.MAX_ANG_ACCEL
        )
    }

    fun turnAsync(angle: Double) {
        trajectorySequenceRunner.followTrajectorySequenceAsync(
            trajectorySequenceBuilder(poseEstimate)
                .turn(angle)
                .build()
        )
    }

    fun turn(angle: Double) {
        turnAsync(angle)
        waitForIdle()
    }

    private fun followTrajectoryAsync(trajectory: Trajectory) {
        trajectorySequenceRunner.followTrajectorySequenceAsync(
            trajectorySequenceBuilder(trajectory.start())
                .addTrajectory(trajectory)
                .build()
        )
    }

    fun followTrajectory(trajectory: Trajectory) {
        followTrajectoryAsync(trajectory)
        waitForIdle()
    }

    fun followTrajectorySequenceAsync(trajectorySequence: TrajectorySequence?) {
        trajectorySequenceRunner.followTrajectorySequenceAsync(trajectorySequence)
    }

    fun followTrajectorySequence(trajectorySequence: TrajectorySequence?) {
        followTrajectorySequenceAsync(trajectorySequence)
        waitForIdle()
    }

    val lastError: Pose2d
        get() = trajectorySequenceRunner.lastPoseError

    fun update() {
        updatePoseEstimate()
        val signal = trajectorySequenceRunner.update(poseEstimate, poseVelocity)
        signal?.let { setDriveSignal(it) }
    }

    private fun waitForIdle() {
        while (!Thread.currentThread().isInterrupted && isBusy) update()
    }

    val isBusy: Boolean
        get() = trajectorySequenceRunner.isBusy

//    fun setMode(runMode: RunMode?) {
//        for (motor in motors) {
//            motor.mode = runMode
//        }
//    }

    fun setWeightedDrivePower(drivePower: Pose2d) {
        var vel = drivePower
        if ((abs(drivePower.x) + abs(drivePower.y) + abs(drivePower.heading)) > 1) {
            // re-normalize the powers according to the weights
            val denominator = VX_WEIGHT * abs(drivePower.x) + VY_WEIGHT * abs(
                drivePower.y
            ) + OMEGA_WEIGHT * abs(drivePower.heading)
            vel = Pose2d(
                VX_WEIGHT * drivePower.x,
                VY_WEIGHT * drivePower.y,
                OMEGA_WEIGHT * drivePower.heading
            ).div(denominator)
        }
        setDrivePower(vel)
    }

    override fun getWheelPositions(): List<Double> {
        lastEncPositions.clear()
        val wheelPositions: MutableList<Double> = ArrayList()
        for (motor in hardware.motors) {
            val position = motor.currentPosition
            lastEncPositions.add(position)
            wheelPositions.add(DriveConstants.encoderTicksToInches(position.toDouble()))
        }
        return wheelPositions
    }

    override fun getWheelVelocities(): List<Double> {
        lastEncVels.clear()
        val wheelVelocities: MutableList<Double> = ArrayList()
        for (motor in hardware.motors) {
            val vel = motor.velocity.toInt()
            lastEncVels.add(vel)
            wheelVelocities.add(DriveConstants.encoderTicksToInches(vel.toDouble()))
        }
        return wheelVelocities
    }

    override fun setMotorPowers(frontLeft: Double, rearLeft: Double, rearRight: Double, frontRight: Double) {
        hardware.setMotorPowers(frontLeft, rearLeft, rearRight, frontRight)
    }

    override val rawExternalHeading: Double
        get() = hardware.rawExternalHeading

    override fun getExternalHeadingVelocity(): Double =
        hardware.externalHeadingVelocity

    companion object {
        var TRANSLATIONAL_PID = PIDCoefficients(0.0, 0.0, 0.0)
        var HEADING_PID = PIDCoefficients(0.0, 0.0, 0.0)
        var LATERAL_MULTIPLIER = 1.0
        var VX_WEIGHT = 1.0
        var VY_WEIGHT = 1.0
        var OMEGA_WEIGHT = 1.0
        private val VEL_CONSTRAINT = getVelocityConstraint(
            DriveConstants.MAX_VEL,
            DriveConstants.MAX_ANG_VEL,
            DriveConstants.TRACK_WIDTH
        )
        private val ACCEL_CONSTRAINT = getAccelerationConstraint(DriveConstants.MAX_ACCEL)
        private fun getVelocityConstraint(
            maxVel: Double,
            maxAngularVel: Double,
            trackWidth: Double
        ): TrajectoryVelocityConstraint {
            return MinVelocityConstraint(
                listOf(
                    AngularVelocityConstraint(maxAngularVel),
                    MecanumVelocityConstraint(maxVel, trackWidth)
                )
            )
        }

        private fun getAccelerationConstraint(maxAccel: Double): TrajectoryAccelerationConstraint {
            return ProfileAccelerationConstraint(maxAccel)
        }
    }
}