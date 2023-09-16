package org.ftc8580.meepmeeptesting

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder

class MeepMeepTesting {
    fun main() {
        val meepMeep = MeepMeep(800)

        val cdBot = DefaultBotBuilder(meepMeep)
            .setConstraints(60.0, 60.0, Math.toRadians(180.0), Math.toRadians(180.0), 15.0)
            .followTrajectorySequence { drive ->
                drive.trajectorySequenceBuilder(Pose2d(0.0, 0.0, 0.0))
                    .forward(30.0)
                    .turn(Math.toRadians(90.0))
                    .forward(30.0)
                    .turn(Math.toRadians(90.0))
                    .forward(30.0)
                    .turn(Math.toRadians(90.0))
                    .forward(30.0)
                    .turn(Math.toRadians(90.0))
                    .build()
            }

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
            .setDarkMode(true)
            .setBackgroundAlpha(0.95f)
            .addEntity(cdBot)
            .start()
    }
}