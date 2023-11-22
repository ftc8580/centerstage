package org.firstinspires.ftc.teamcode.command

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.util.isTimedOut

class Wait(private val timeMs: Double) : CommandBase() {
    private val runtime = ElapsedTime()

    override fun initialize() {
        runtime.reset()
    }

    override fun isFinished(): Boolean = runtime.isTimedOut(timeMs)
}