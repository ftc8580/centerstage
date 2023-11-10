package org.firstinspires.ftc.teamcode.opmode.auton

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.config.ParkPosition

@Suppress("UNUSED")
@Autonomous(group = "Blue")
class BlueWingSideOutsideAutonParkOutside : WingSideAuton(Alliance.BLUE, ParkPosition.OUTSIDE, ParkPosition.OUTSIDE)