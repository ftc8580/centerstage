package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.util.ElapsedTime

fun ElapsedTime.isTimedOut(timeoutMs: Double): Boolean {
    return this.milliseconds() > timeoutMs
}