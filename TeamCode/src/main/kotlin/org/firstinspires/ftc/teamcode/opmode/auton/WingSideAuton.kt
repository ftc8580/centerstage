package org.firstinspires.ftc.teamcode.opmode.auton

import org.firstinspires.ftc.teamcode.config.ParkPosition
import org.firstinspires.ftc.teamcode.opmode.OpModeBase

abstract class WingSideAuton(private val alliance: Alliance, private val parkPosition: ParkPosition) : OpModeBase() {
    override fun initialize() {
        initHardware(true)

        // TODO
    }
}