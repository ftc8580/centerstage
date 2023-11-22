package org.firstinspires.ftc.teamcode.opmode.auton

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.config.ParkPosition

@Suppress("UNUSED")
@Autonomous(group = "Blue")
class PauseBlueBackdropSideAutonOutside : BackdropSideAuton(Alliance.BLUE, ParkPosition.OUTSIDE, true)