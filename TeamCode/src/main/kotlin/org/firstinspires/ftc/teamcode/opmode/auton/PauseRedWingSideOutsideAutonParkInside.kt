package org.firstinspires.ftc.teamcode.opmode.auton

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.config.ParkPosition

@Suppress("UNUSED")
@Autonomous(group = "Red")
class PauseRedWingSideOutsideAutonParkInside : WingSideAuton(Alliance.RED, ParkPosition.OUTSIDE, ParkPosition.INSIDE, true)