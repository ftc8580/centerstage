package org.ftc8580.meepmeeptesting;



import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepKylerBB {
 public static double apriltagspacinginches = 6;


    public static void main(String[] args) {

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot dimensions: width, height
                .setDimensions(14.0, 17.0)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(400, 40
                        , Math.toRadians(208.94533694100002), Math.toRadians(180), 14.3)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(12, 63.5, Math.toRadians(270)))
                                //TODO if teampproplocation=center
                                .lineToLinearHeading(new Pose2d(12.0, 32.5, Math.toRadians(270)))
                                .waitSeconds(0)
                                //TODO else if teamproplocation=right
                                .lineToLinearHeading(new Pose2d(6.0, 36, Math.toRadians(225)))
                                //TODO else teamproplocation=left
                                .lineToLinearHeading(new Pose2d(18.0, 36, Math.toRadians(315)))
                                //TODO Rollers backward very slowly
                                .lineToLinearHeading(new Pose2d(12.0, 48, Math.toRadians(270)))
                                .turn(Math.toRadians(-90))
                                //TODO Extend ExtenderHeightDeliverLow
                                //TODO if teamproplocation=center
                                .lineToLinearHeading(new Pose2d(49.5, 36, Math.toRadians(180)))
                                .lineToLinearHeading(new Pose2d(51.5, 36, Math.toRadians(180)))
                                //TODO create variable apriltagspacinginches=6
                                //TODO else if teamproplocation=right
                                .lineToLinearHeading(new Pose2d(49.5, 36-apriltagspacinginches, Math.toRadians(180)))
                                .lineToLinearHeading(new Pose2d(51.5, 36-apriltagspacinginches, Math.toRadians(180)))
                                //TODO else teamproplocation=left
                                .lineToLinearHeading(new Pose2d(49.5, 36+apriltagspacinginches, Math.toRadians(180)))
                                .lineToLinearHeading(new Pose2d(51.5, 36+apriltagspacinginches, Math.toRadians(180)))
                                //TODO open bucketservo to deliver yellow pixel
                                //TODO Create Variable AutonPath = INSIDE/OUTSIDE
                                //TODO IF AutonPath= INSIDE
                                .lineToLinearHeading(new Pose2d(49.5, 12, Math.toRadians(180)))
                                .lineToLinearHeading(new Pose2d(62, 12, Math.toRadians(180)))
                                //TODO ELSE AutonPath= OUTSIDE
                                .lineToLinearHeading(new Pose2d(49.5, 60, Math.toRadians(180)))
                                .lineToLinearHeading(new Pose2d(62, 60, Math.toRadians(180)))




                                .build()
                );

//        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
//                // Set bot dimensions: width, height
//                .setDimensions(14.0, 17.0)
//                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
//                .setConstraints(45, 45, Math.toRadians(208.94533694100002), Math.toRadians(180), 14.3)
//                .followTrajectorySequence(drive ->
//                        drive.trajectorySequenceBuilder(new Pose2d(-36, -62, Math.toRadians(90)))
//                                .lineToLinearHeading(new Pose2d(-36, -32.5, Math.toRadians(180)))
//                                .waitSeconds(2)
//                                .lineToLinearHeading(new Pose2d(-54, -35, Math.toRadians(180)))
//                                .waitSeconds(2)
//                                .lineToLinearHeading(new Pose2d(-36, -60, Math.toRadians(0)))
//                                .lineToConstantHeading(new Vector2d(60, -60))
//                                .waitSeconds(2)
//                                .build()
//                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}