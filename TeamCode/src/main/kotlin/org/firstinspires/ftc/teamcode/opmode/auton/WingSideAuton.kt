package org.firstinspires.ftc.teamcode.opmode.auton

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.teamcode.command.FollowTrajectorySequence
import org.firstinspires.ftc.teamcode.command.Wait
import org.firstinspires.ftc.teamcode.command.delivery.DeliverPixelStageOne
import org.firstinspires.ftc.teamcode.command.delivery.DeliverPixelStageTwo
import org.firstinspires.ftc.teamcode.command.delivery.GoToBottomPosition
import org.firstinspires.ftc.teamcode.command.delivery.ResetViperRunMode
import org.firstinspires.ftc.teamcode.command.delivery.SetViperBottom
import org.firstinspires.ftc.teamcode.config.ParkPosition
import org.firstinspires.ftc.teamcode.opmode.OpModeBase
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.vision.RandomizedSpikeLocation

abstract class WingSideAuton(
    private val alliance: Alliance,
    private val traversePosition: ParkPosition,
    private val parkPosition: ParkPosition,
    private val pause: Boolean = false
) : OpModeBase() {
    override fun initialize() {
        initHardware(true)

        val pauseTimeMs = if (pause) 10000.0 else 0.0

        var spikeLocation = RandomizedSpikeLocation.UNKNOWN
        var spikePose: Pose2d? = null
        var deliveryPose: Pose2d? = null
        var clearBoardPose: Pose2d? = null
        var spikeTrajectory: TrajectorySequence? = null
        var clearBoardTrajectory: TrajectorySequence? = null

        // Start Pose
        val startingPose = Pose2d(
            STARTING_X,
            alliance.adjust(63.5),
            Math.toRadians(alliance.adjust(270.0))
        )

        val preSpikePose = Pose2d(
            STARTING_X,
            alliance.adjust(58.5),
            Math.toRadians(alliance.adjust(270.0))
        )

        // Spike Delivery Poses
        val spikePosition1Pose = Pose2d(
            STARTING_X + alliance.adjust(APRIL_TAG_SPACING_INCHES),
            alliance.adjust(36.0),
            Math.toRadians(alliance.adjust(CENTER_SPIKE_DEGREES) + SIDE_SPIKE_ROTATION_DEGREES)
        )
        val spikePosition2Pose = Pose2d(
            STARTING_X,
            alliance.adjust(33.5),
            Math.toRadians(alliance.adjust(CENTER_SPIKE_DEGREES))
        )
        val spikePosition3Pose = Pose2d(
            STARTING_X - alliance.adjust(APRIL_TAG_SPACING_INCHES),
            alliance.adjust(36.0),
            Math.toRadians(alliance.adjust(CENTER_SPIKE_DEGREES) - SIDE_SPIKE_ROTATION_DEGREES)
        )

        // Pose to make sure we don't run into the dropped pixel
        val clearSpikePose = Pose2d(
            STARTING_X,
            alliance.adjust(48.0),
            Math.toRadians(alliance.adjust(-90.0))
        )

        // Delivery positions
        val deliverPosition1Pose = Pose2d(
            51.5,
            alliance.adjust(35.0) + APRIL_TAG_SPACING_INCHES,
            Math.toRadians(180.0)
        )
        val deliverPosition2Pose = Pose2d(
            51.5,
            alliance.adjust(35.0),
            Math.toRadians(180.0)
        )
        val deliverPosition3Pose = Pose2d(
            51.5,
            alliance.adjust(35.0) - APRIL_TAG_SPACING_INCHES,
            Math.toRadians(180.0)
        )

        val clearBoardPosition1Pose = Pose2d(
            deliverPosition1Pose.x - 2.0,
            deliverPosition1Pose.y,
            deliverPosition1Pose.heading
        )
        val clearBoardPosition2Pose = Pose2d(
            deliverPosition2Pose.x - 2.0,
            deliverPosition2Pose.y,
            deliverPosition2Pose.heading
        )
        val clearBoardPosition3Pose = Pose2d(
            deliverPosition3Pose.x - 2.0,
            deliverPosition3Pose.y,
            deliverPosition3Pose.heading
        )

        val preSpikeTrajectory = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(preSpikePose)
            .build()

        // Spike delivery positions
        val spikePosition1 = mecanumDrive.trajectorySequenceBuilder(preSpikePose)
            .lineToLinearHeading(spikePosition1Pose)
            .build()

        val spikePosition2 = mecanumDrive.trajectorySequenceBuilder(preSpikePose)
            .lineToLinearHeading(spikePosition2Pose)
            .build()

        val spikePosition3 = mecanumDrive.trajectorySequenceBuilder(preSpikePose)
            .lineToLinearHeading(spikePosition3Pose)
            .build()

        // Clear backdrop positions
        val clearBoardPosition1 = mecanumDrive.trajectorySequenceBuilder(deliverPosition1Pose)
            .lineToLinearHeading(clearBoardPosition1Pose)
            .build()

        val clearBoardPosition2 = mecanumDrive.trajectorySequenceBuilder(deliverPosition2Pose)
            .lineToLinearHeading(clearBoardPosition2Pose)
            .build()

        val clearBoardPosition3 = mecanumDrive.trajectorySequenceBuilder(deliverPosition3Pose)
            .lineToLinearHeading(clearBoardPosition3Pose)
            .build()

        mecanumDrive.poseEstimate = startingPose

        /**
         * Get spike delivery position
         */
        while (!isStarted && !isStopRequested) {
            multiTelemetry.clearAll()
            spikeLocation = tfod?.getRandomizedSpikeLocation() ?: RandomizedSpikeLocation.UNKNOWN
            tfod?.telemetryTfod()
            multiTelemetry.update()
        }

        // Shut down camera to save cycles
        try { tfod?.suspend() } catch(_: Exception) {}

        when (spikeLocation) {
            RandomizedSpikeLocation.RIGHT,
            RandomizedSpikeLocation.UNKNOWN -> {
                spikePose = spikePosition3Pose
                spikeTrajectory = spikePosition3
                deliveryPose = deliverPosition3Pose
                clearBoardPose = clearBoardPosition3Pose
                clearBoardTrajectory = clearBoardPosition3
            }
            RandomizedSpikeLocation.CENTER -> {
                spikePose = spikePosition2Pose
                spikeTrajectory = spikePosition2
                deliveryPose = deliverPosition2Pose
                clearBoardPose = clearBoardPosition2Pose
                clearBoardTrajectory = clearBoardPosition2
            }
            RandomizedSpikeLocation.LEFT -> {
                spikePose = spikePosition1Pose
                spikeTrajectory = spikePosition1
                deliveryPose = deliverPosition1Pose
                clearBoardPose = clearBoardPosition1Pose
                clearBoardTrajectory = clearBoardPosition1
            }
        }

        val clearPixelTrajectory = mecanumDrive.trajectorySequenceBuilder(spikePose)
            .lineToLinearHeading(clearSpikePose)
            .build()

        val deliverTrajectory = if (traversePosition == ParkPosition.INSIDE) {
            if (spikeLocation == RandomizedSpikeLocation.CENTER) {
                mecanumDrive.trajectorySequenceBuilder(clearSpikePose)
                    .lineToLinearHeading(Pose2d(-55.0, alliance.adjust(36.0), Math.toRadians(alliance.adjust(-180.0))))
                    .lineToLinearHeading(Pose2d(-55.0, alliance.adjust(12.0), Math.toRadians(alliance.adjust(-180.0))))
                    .lineToLinearHeading(Pose2d(24.0, alliance.adjust(12.0), Math.toRadians(alliance.adjust(-180.0))))
                    .lineToLinearHeading(deliveryPose)
                    .build()
            } else {
                mecanumDrive.trajectorySequenceBuilder(clearSpikePose)
                    .lineToLinearHeading(Pose2d(-36.0, alliance.adjust(12.0), Math.toRadians(alliance.adjust(270.0))))
                    .turn(Math.toRadians(alliance.adjust(-90.0)))
                    .lineToLinearHeading(Pose2d(24.0, alliance.adjust(12.0), Math.toRadians(alliance.adjust(-180.0))))
                    .lineToLinearHeading(deliveryPose)
                    .build()
            }
        } else {
            mecanumDrive.trajectorySequenceBuilder(clearSpikePose)
                .lineToLinearHeading(Pose2d(-36.0, alliance.adjust(60.0), Math.toRadians(alliance.adjust(-180.0))))
                .lineToLinearHeading(Pose2d(24.0, alliance.adjust(60.0), Math.toRadians(alliance.adjust(-180.0))))
                .lineToLinearHeading(deliveryPose)
                .build()
        }

        val parkInsideTrajectory = mecanumDrive.trajectorySequenceBuilder(clearBoardPose)
            .lineToLinearHeading(Pose2d(48.0, alliance.adjust(12.0), Math.toRadians(180.0)))
            .lineToLinearHeading(Pose2d(60.0, alliance.adjust(12.0), Math.toRadians(180.0)))
            .build()

        val parkOutsideTrajectory = mecanumDrive.trajectorySequenceBuilder(clearBoardPose)
            .lineToLinearHeading(Pose2d(48.0, alliance.adjust(60.0), Math.toRadians(180.0)))
            .lineToLinearHeading(Pose2d(60.0, alliance.adjust(60.0), Math.toRadians(180.0)))
            .build()

        val parkTrajectory = when (parkPosition) {
            ParkPosition.INSIDE -> parkInsideTrajectory
            ParkPosition.OUTSIDE -> parkOutsideTrajectory
        }

        schedule(
            SequentialCommandGroup(
                Wait(pauseTimeMs),
                FollowTrajectorySequence(mecanumDrive, preSpikeTrajectory),
                FollowTrajectorySequence(mecanumDrive, spikeTrajectory),
                FollowTrajectorySequence(mecanumDrive, clearPixelTrajectory),
                GoToBottomPosition(deliverySubsystem!!),
                FollowTrajectorySequence(mecanumDrive, deliverTrajectory),
                DeliverPixelStageOne(deliverySubsystem!!),
                FollowTrajectorySequence(mecanumDrive, clearBoardTrajectory),
                DeliverPixelStageTwo(deliverySubsystem!!),
                FollowTrajectorySequence(mecanumDrive, parkTrajectory),
                ResetViperRunMode(deliverySubsystem!!)
            )
        )
    }

    companion object {
        private const val APRIL_TAG_SPACING_INCHES = 5.5
        private const val CENTER_SPIKE_DEGREES = 270.0
        private const val SIDE_SPIKE_ROTATION_DEGREES = 45.0
        private const val STARTING_X = -36.0
    }
}