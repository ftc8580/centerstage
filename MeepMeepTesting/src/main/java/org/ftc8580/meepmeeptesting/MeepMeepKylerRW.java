package org.ftc8580.meepmeeptesting;



import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepKylerRW {
 public static double apriltagspacinginches = 6;


    public static void main(String[] args) {

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot dimensions: width, height
                .setDimensions(14.0, 17.0)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(400, 80


                        , Math.toRadians(208.94533694100002), Math.toRadians(180), 14.3)
                .followTrajectorySequence(drive ->
                                drive.trajectorySequenceBuilder(new Pose2d(-36, -61.5, Math.toRadians(90)))
                                        //TODO if teampproplocation=center
                                        .lineToLinearHeading(new Pose2d(-36.0, -32.5, Math.toRadians(-270)))
                                        .waitSeconds(0)
                                        //TODO else if teamproplocation=right
                                        .lineToLinearHeading(new Pose2d(-30.0, -36, Math.toRadians(-315)))
                                        //TODO else teamproplocation=left
                                        .lineToLinearHeading(new Pose2d(-42.0, -36, Math.toRadians(-225)))
                                        //TODO Rollers backward very slowly
                                        .lineToLinearHeading(new Pose2d(-36.0, -48, Math.toRadians(-270)))
                                        .turn(Math.toRadians(90))
                                        //TODO Extend ExtenderHeightDeliverLow

                                        //align to truss
                                        //TODO if AutonPath=inside for teamproplocation= center
                                        .lineToLinearHeading(new Pose2d(-60.0, -36, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(-60.0, -12, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(12.0, -12, Math.toRadians(180)))

                                        //TODO if AutonPath=inside for teamproplocation=right or left
                                        .lineToLinearHeading(new Pose2d(-36.0, -12, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(12.0, -12, Math.toRadians(180)))

                                        //TODO if AutonPath=outside
                                        .lineToLinearHeading(new Pose2d(-36.0, -60, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(12.0, -60, Math.toRadians(180)))

                                        //TODO if teamproplocation=center
                                        .lineToLinearHeading(new Pose2d(49.5, -36, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(51.5, -36, Math.toRadians(180)))
                                        //TODO create variable apriltagspacinginches=6
                                        //TODO else if teamproplocation=right
                                        .lineToLinearHeading(new Pose2d(49.5, -36-apriltagspacinginches, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(51.5, -36-apriltagspacinginches, Math.toRadians(180)))
                                        //TODO else teamproplocation=left
                                        .lineToLinearHeading(new Pose2d(49.5, -36+apriltagspacinginches, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(51.5, -36+apriltagspacinginches, Math.toRadians(180)))
                                        //TODO open bucketservo to deliver yellow pixel
                                        //TODO Create Variable AutonPath = INSIDE/OUTSIDE
                                        //TODO IF AutonPath= INSIDE
                                        .lineToLinearHeading(new Pose2d(49.5, -12, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(62, -12, Math.toRadians(180)))
                                        //TODO ELSE AutonPath= OUTSIDE
                                        .lineToLinearHeading(new Pose2d(49.5, -60, Math.toRadians(180)))
                                        .lineToLinearHeading(new Pose2d(62, -60, Math.toRadians(180)))




                                        .build()
                );


        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}