package org.firstinspires.ftc.teamcode.opmode.auton

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.teamcode.command.FollowTrajectorySequence
import org.firstinspires.ftc.teamcode.command.delivery.DeliverPixel
import org.firstinspires.ftc.teamcode.command.intake.EjectPixel
import org.firstinspires.ftc.teamcode.opmode.OpModeBase
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.vision.RandomizedSpikeLocation
import org.firstinspires.ftc.teamcode.vision.TensorFlowObjectDetection

abstract class BackstageSideAuton(
    private val alliance: Alliance
) : OpModeBase() {
    override fun initialize() {
        initHardware(true)

        val tfod = TensorFlowObjectDetection(hardware, multiTelemetry)
        var spikeLocation = RandomizedSpikeLocation.UNKNOWN
        var spikeTrajectory: TrajectorySequence? = null
        var deliverTrajectory: TrajectorySequence? = null
        var parkTrajectory: TrajectorySequence? = null

        val startingPose = Pose2d(
            12.0,
            alliance.adjust(-61.5),
            Math.toRadians(alliance.adjust(90.0))
        )

        val spikePosition1Pose = Pose2d(
            12.0,
            alliance.adjust(-32.5),
            Math.toRadians(alliance.adjust(180.0))
        )

        val spikePosition2Pose = Pose2d(
            12.0,
            alliance.adjust(-34.0),
            Math.toRadians(alliance.adjust(90.0))
        )

        val spikePosition3Pose = Pose2d(
            12.0,
            alliance.adjust(-32.5),
            Math.toRadians(alliance.adjust(0.0))
        )

        val deliverPosition1Pose = Pose2d(
            49.5,
            alliance.adjust(-32.5),
            Math.toRadians(alliance.adjust(180.0))
        )

        val deliverPosition2Pose = Pose2d(
            49.5,
            alliance.adjust(-35.0),
            Math.toRadians(alliance.adjust(180.0))
        )

        val deliverPosition3Pose = Pose2d(
            49.5,
            alliance.adjust(-42.0),
            Math.toRadians(alliance.adjust(180.0))
        )

        val spikePosition1 = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(spikePosition1Pose)
            .build()

        val spikePosition2 = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(spikePosition2Pose)
            .build()

        val spikePosition3 = mecanumDrive.trajectorySequenceBuilder(startingPose)
            .lineToLinearHeading(spikePosition3Pose)
            .build()

        val deliverPosition1 = mecanumDrive.trajectorySequenceBuilder(spikePosition1Pose)
            .lineToLinearHeading(deliverPosition1Pose)
            .build()

        val deliverPosition2 = mecanumDrive.trajectorySequenceBuilder(spikePosition2Pose)
            .lineToLinearHeading(deliverPosition2Pose)
            .build()

        val deliverPosition3 = mecanumDrive.trajectorySequenceBuilder(spikePosition3Pose)
            .lineTo(Vector2d(12.0, alliance.adjust(-45.0)))
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
                spikeTrajectory = spikePosition3
                deliverTrajectory = deliverPosition3
            }
            RandomizedSpikeLocation.CENTER,
            RandomizedSpikeLocation.UNKNOWN-> {
                spikeTrajectory = spikePosition2
                deliverTrajectory = deliverPosition2
            }
            RandomizedSpikeLocation.LEFT -> {
                spikeTrajectory = spikePosition1
                deliverTrajectory = deliverPosition1
            }
        }

        schedule(SequentialCommandGroup(
            FollowTrajectorySequence(mecanumDrive, spikeTrajectory),
            EjectPixel(transferSubsystem!!),
            FollowTrajectorySequence(mecanumDrive, deliverTrajectory),
            DeliverPixel(deliverySubsystem!!),
            // TODO: Uncomment when park trajectory is defined
            // FollowTrajectorySequence(mecanumDrive, parkTrajectory)
        ))
    }
}