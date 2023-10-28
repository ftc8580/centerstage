package org.ftc8580.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

//        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
//                // Set bot dimensions: width, height
//                .setDimensions(14.0, 17.0)
//                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
//                .setConstraints(45, 45, Math.toRadians(208.94533694100002), Math.toRadians(180), 14.3)
//                .followTrajectorySequence(drive ->
//                        drive.trajectorySequenceBuilder(new Pose2d(12, -61.5, Math.toRadians(90)))
//                                .lineToLinearHeading(new Pose2d(12, -32.5, Math.toRadians(180)))
//                                .waitSeconds(2)
//                                .lineToLinearHeading(new Pose2d(49.5, -30, Math.toRadians(180)))
//                                .waitSeconds(2)
//                                .splineToLinearHeading(new Pose2d(59, -58, Math.toRadians(180)), Math.toRadians(45))
//                                .build()
//                );

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot dimensions: width, height
                .setDimensions(14.0, 17.0)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(45, 45, Math.toRadians(208.94533694100002), Math.toRadians(180), 14.3)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -62, Math.toRadians(90)))
                                .lineToLinearHeading(new Pose2d(-36, -32.5, Math.toRadians(180)))
                                .waitSeconds(2)
                                .lineToLinearHeading(new Pose2d(-54, -35, Math.toRadians(180)))
                                .waitSeconds(2)
                                .lineToLinearHeading(new Pose2d(-36, -60, Math.toRadians(0)))
                                .lineToConstantHeading(new Vector2d(60, -60))
                                .waitSeconds(2)
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}