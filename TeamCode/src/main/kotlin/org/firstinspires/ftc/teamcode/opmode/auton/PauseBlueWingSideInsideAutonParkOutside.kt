package org.firstinspires.ftc.teamcode.opmode.auton

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.config.ParkPosition

@Suppress("UNUSED")
@Autonomous(group = "Blue")
class PauseBlueWingSideInsideAutonParkOutside : WingSideAuton(Alliance.BLUE, ParkPosition.INSIDE, ParkPosition.OUTSIDE, true)