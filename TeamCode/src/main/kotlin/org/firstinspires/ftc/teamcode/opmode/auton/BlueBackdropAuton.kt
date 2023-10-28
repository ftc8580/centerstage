package org.firstinspires.ftc.teamcode.opmode.auton

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.teamcode.command.FollowTrajectorySequence
import org.firstinspires.ftc.teamcode.command.delivery.DeliverPixel
import org.firstinspires.ftc.teamcode.command.intake.EjectPixel
import org.firstinspires.ftc.teamcode.config.ParkPosition
import org.firstinspires.ftc.teamcode.opmode.OpModeBase
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.vision.RandomizedSpikeLocation
import org.firstinspires.ftc.teamcode.vision.TensorFlowObjectDetection

abstract class BlueBackdropAuton(private val parkPosition: ParkPosition) : OpModeBase() {
    override fun initialize() {
        initHardware(true)

        val tfod = TensorFlowObjectDetection(hardware, multiTelemetry)
        var spikeLocation = RandomizedSpikeLocation.UNKNOWN
        var spikePose: Pose2d? = null
        var deliveryPose: Pose2d? = null
        var spikeTrajectory: TrajectorySequence? = null
        var deliverTrajectory: TrajectorySequence? = null

        // Start Pose
        val startingPose = Pose2d(12.0, 63.5, Math.toRadians(270.0))

        // Spike Delivery Poses
        val spikePosition1Pose = Pose2d(18.0, 36.0, Math.toRadians(315.0))
        val spikePosition2Pose = Pose2d(12.0, 32.5, Math.toRadians(270.0))
        val spikePosition3Pose = Pose2d(6.0, 36.0, Math.toRadians(225.0))

        // Pose to make sure we don't run into the dropped pixel
        val clearSpikePose = Pose2d(12.0, 48.0, Math.toRadians(270.0))

        // Delivery positions
        val deliverPosition1Pose = Pose2d(51.5, 36.0 + APRIL_TAG_SPACING_INCHES, Math.toRadians(180.0))
        val deliverPosition2Pose = Pose2d(51.5, 36.0, Math.toRadians(180.0))
        val deliverPosition3Pose = Pose2d(51.5, 36.0 - APRIL_TAG_SPACING_INCHES, Math.toRadians(180.0))

        // Spike delivery positions
        val spikePosition1 = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(spikePosition1Pose)
            .build()

        val spikePosition2 = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(spikePosition2Pose)
            .build()

        val spikePosition3 = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(spikePosition3Pose)
            .build()

        // Backdrop delivery positions
        val deliverPosition1 = mecanumDrive.trajectorySequenceBuilder(spikePosition1Pose)
            .lineToLinearHeading(deliverPosition1Pose)
            .build()

        val deliverPosition2 = mecanumDrive.trajectorySequenceBuilder(spikePosition2Pose)
            .lineToLinearHeading(deliverPosition2Pose)
            .build()

        val deliverPosition3 = mecanumDrive.trajectorySequenceBuilder(spikePosition3Pose)
            .lineToLinearHeading(deliverPosition3Pose)
            .build()

        mecanumDrive.poseEstimate = startingPose

        /**
         * Get spike delivery position
         */
        while (!isStarted && !isStopRequested) {
            spikeLocation = tfod.getRandomizedSpikeLocation()
            multiTelemetry.addData("Spike Location:", spikeLocation)
            multiTelemetry.update()
        }

        // Shut down camera to save cycles
        tfod.suspend()

        when (spikeLocation) {
            RandomizedSpikeLocation.RIGHT -> {
                spikePose = spikePosition3Pose
                spikeTrajectory = spikePosition3
                deliveryPose = deliverPosition3Pose
                deliverTrajectory = deliverPosition3
            }
            RandomizedSpikeLocation.CENTER,
            RandomizedSpikeLocation.UNKNOWN-> {
                spikePose = spikePosition2Pose
                spikeTrajectory = spikePosition2
                deliveryPose = deliverPosition2Pose
                deliverTrajectory = deliverPosition2
            }
            RandomizedSpikeLocation.LEFT -> {
                spikePose = spikePosition1Pose
                spikeTrajectory = spikePosition1
                deliveryPose = deliverPosition1Pose
                deliverTrajectory = deliverPosition1
            }
        }

        val clearPixelTrajectory = mecanumDrive.trajectorySequenceBuilder(spikePose)
            .lineToLinearHeading(clearSpikePose)
            .build()

        val parkInsideTrajectory = mecanumDrive.trajectorySequenceBuilder(deliveryPose)
            .lineToLinearHeading(Pose2d(49.5, 12.0, Math.toRadians(180.0)))
            .lineToLinearHeading(Pose2d(62.0, 12.0, Math.toRadians(180.0)))
            .build()

        val parkOutsideTrajectory = mecanumDrive.trajectorySequenceBuilder(deliveryPose)
            .lineToLinearHeading(Pose2d(49.5, 60.0, Math.toRadians(180.0)))
            .lineToLinearHeading(Pose2d(62.0, 60.0, Math.toRadians(180.0)))
            .build()

        val parkTrajectory = when (parkPosition) {
            ParkPosition.INSIDE -> parkInsideTrajectory
            ParkPosition.OUTSIDE -> parkOutsideTrajectory
        }

        schedule(
            SequentialCommandGroup(
                FollowTrajectorySequence(mecanumDrive, spikeTrajectory),
                EjectPixel(transferSubsystem!!),
                FollowTrajectorySequence(mecanumDrive, clearPixelTrajectory),
                FollowTrajectorySequence(mecanumDrive, deliverTrajectory),
                DeliverPixel(deliverySubsystem!!),
                FollowTrajectorySequence(mecanumDrive, parkTrajectory)
            )
        )
    }

    companion object {
        private const val APRIL_TAG_SPACING_INCHES = 6.0
    }
}