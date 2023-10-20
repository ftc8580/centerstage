package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.util.ElapsedTime

class CDRuntime : ElapsedTime() {
    fun isTimedOut(timeoutMs: Double): Boolean {
        return this.milliseconds() > timeoutMs
    }
}